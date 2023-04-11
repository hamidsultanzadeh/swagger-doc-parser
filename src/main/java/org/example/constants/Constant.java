package org.example.constants;

public final class Constant {

    public final static class Key {

        public final static String INFO = "info";
        public final static String CONTACT = "contact";
        public final static String EMAIL = "email";
        public final static String NAME = "name";
        public final static String URL = "url";
        public final static String DESCRIPTION = "description";
        public final static String LICENSE = "license";
        public final static String TERMS_OF_SERVICE = "termsOfService";
        public final static String TITLE = "title";
        public final static String VERSION = "version";

        private Key() {
        }
    }

    public final static String REPLACE_LOG_FILE_NAME = "log.txt";
    public final static String REPLACE_SENTENCE = "replace %s to %s\n";
    public final static String DEFAULT_STRING = "";
    public final static String REPLACE_PATTERN = "[-_]";
    public final static String REF = "$ref";
    public final static String RESULT_FILE_NAME = "result.yaml";
    public final static String SCHEMAS_KEY = "schemas";
    public final static String COMPONENTS_KEY = "components";
    public final static String PATHS_KEY = "paths";
    public final static String YAML_EXTENSION = ".yaml";
    public final static String PATTERN = "*/*";
    public final static String CONTENT = "content";
    public final static String CONTENT_TYPE = "application/json";

    public final static class Info {

        public final static String CONTACT_EMAIL = "support@finik.pro";
        public final static String CONTACT_NAME = "API Support";
        public final static String CONTACT_URL = "http://finik.pro/support";
        public final static String DESCRIPTION = "IBS WIZZ Api";
        public final static String LICENSE_NAME = "Apache 2.0";
        public final static String LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0.html";
        public final static String TERMS_OF_SERVICE = "http://finik.pro/terms/";
        public final static String TITLE = "IBS WIZZ Api";
        public final static String VERSION = "0.1.0";


        private Info() {
        }
    }

    private Constant() {
    }
}
