package johnlest.tools;

public class Tools {
    public static Boolean isNullOrEmpty(Object obj) {
        if(obj == null || obj.toString().length() == 0)
            return true;
        return false;
    }
}
