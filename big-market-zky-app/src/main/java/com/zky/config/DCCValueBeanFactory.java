package com.zky.config;

import com.zky.types.annotations.DCCValue;
import com.zky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ZhangKaiYuan
 * @description: 基于 Zookeeper 配置中心实现原理
 * @create: 2025/3/20
 */
@Slf4j
@Configuration
public class DCCValueBeanFactory implements BeanPostProcessor {

    private static final String BASE_CONFIG_PATH = "/big-market-dcc";
    private static final String BASE_CONFIG_PATH_CONFIG = BASE_CONFIG_PATH + "/config";
    private final CuratorFramework client;

    private final Map<String, Object> dccObjGroup = new HashMap<>();


    public DCCValueBeanFactory(CuratorFramework client) throws Exception {
        this.client = client;

        //节点判断
        if(null == client.checkExists().forPath(BASE_CONFIG_PATH_CONFIG)){
            client.create().creatingParentsIfNeeded().forPath(BASE_CONFIG_PATH_CONFIG);
            log.info("DCC 节点监听 base node {} not absent create new done!", BASE_CONFIG_PATH_CONFIG);
        }

        //CuratorCache 是 Curator 提供的一个缓存组件，监听 Zookeeper 节点的数据变化，并缓存节点的最新数据。
        CuratorCache curatorCache = CuratorCache.build(client, BASE_CONFIG_PATH_CONFIG);
        curatorCache.start();

        //给CuratorCache添加一个监听器，数据发生改变时触发。
        curatorCache.listenable().addListener((type, oldData, data) -> {
            switch (type){
                case NODE_CHANGED:
                    String dccValuePath = data.getPath();
                    Object objBean = dccObjGroup.get(dccValuePath);
                    if(null == objBean) return;
                    try {
                        //通过反射获取到objBean对应类中的与dccValuePath相关联的字段。
                        Field field = objBean.getClass().getDeclaredField(dccValuePath.substring(dccValuePath.lastIndexOf("/") + 1));
                        field.setAccessible(true);
                        field.set(objBean, new String(data.getData()));
                        field.setAccessible(false);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    break;
            }
        });
    }


    /**
     * 实现BeanPostProcessor作用：在Bean初始化之前，对带有@DCCValue注解的字段进行处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //获取传入bean对象的类类型
        Class<?> beanClass = bean.getClass();
        //获取该类的所有字段(包括私有字段)
        Field[] fields = beanClass.getDeclaredFields();
        for(Field field : fields){
            //判断当前字段有没有@DCCValue注解
            if(!field.isAnnotationPresent(DCCValue.class)){
                continue;
            }
            //获取该字段的DCCValue对象
            DCCValue dccValue = field.getAnnotation(DCCValue.class);

            String value = dccValue.value();
            if(StringUtils.isBlank(value)){
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }

            String[] splits = value.split(":");
            String key = splits[0];
            String defaultValue = splits.length == 2 ? splits[1] : null;

            try{
                //判断当前节点是否存在，如果不存在则创建 Zookeeper 节点
                String keyPath = BASE_CONFIG_PATH_CONFIG.concat("/").concat(key);
                if(null == client.checkExists().forPath(keyPath)){
                    client.create().creatingParentsIfNeeded().forPath(keyPath);
                    if(StringUtils.isBlank(defaultValue)){
                        field.setAccessible(true);
                        field.set(bean, defaultValue);
                        field.setAccessible(false);
                    }
                    log.info("DCC 节点监听 创建节点 {}", keyPath);
                }else {
                    String configValue = new String(client.getData().forPath(keyPath));
                    if(StringUtils.isBlank(configValue)){
                        field.setAccessible(true);
                        field.set(bean, configValue);
                        field.setAccessible(false);
                        log.info("DCC 节点监听 设置配置 {} {} {}", keyPath, field.getName(), configValue);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            dccObjGroup.put(BASE_CONFIG_PATH_CONFIG.concat("/").concat(key), bean);
        }

        return bean;
    }
}
