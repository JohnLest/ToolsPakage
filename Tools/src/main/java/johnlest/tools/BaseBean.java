package johnlest.tools;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Properties;

public class BaseBean {
    public static <T> Properties toProperties(T t) throws Exception {

        Class<T> c = (Class<T>) t.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(c);
        Properties p = new Properties();

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            String name = pd.getName();
            Object o = pd.getReadMethod().invoke(t);
            if (o != null)
                p.setProperty(name, o == null ? null : o.toString());
        }
        return p;
    }
}
