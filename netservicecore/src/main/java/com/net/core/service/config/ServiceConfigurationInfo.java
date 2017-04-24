package com.net.core.service.config;

import java.util.List;

/**
 * <br> 服务器配置数据
 * Created by xinhong.zhang on 2016/8/1.
 */
public class ServiceConfigurationInfo {

    /**
     * status : 0
     * msg : Success
     * data : {"configuration":[{"key":"new_user_xxx_percent","value":"30"}]}
     * compress : 0
     */

    private String status;
    private String msg;
    private String data;
    private DataEntity dataBody;
    private int compress;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCompress(int compress) {
        this.compress = compress;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getData() {
        return data;
    }

    public DataEntity getDataBody() {
        return dataBody;
    }

    public void setDataBody(DataEntity dataBody) {
        this.dataBody = dataBody;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCompress() {
        return compress;
    }

    public static class DataEntity {
        /**
         * key : new_user_xxx_percent
         * value : 30
         */

        private List<ConfigurationEntity> configuration;

        public void setConfiguration(List<ConfigurationEntity> configuration) {
            this.configuration = configuration;
        }

        public List<ConfigurationEntity> getConfiguration() {
            return configuration;
        }

        public static class ConfigurationEntity {
            private String key;
            private String value;

            public void setKey(String key) {
                this.key = key;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getKey() {
                return key;
            }

            public String getValue() {
                return value;
            }
        }
    }
}