package iovi;

/** Помощник при работе с параметрами приложения*/
public class PropertiesHelper {
    /**
     * <p>Определяет, в продуктивном или тестовом режиме работает приложение.</p>
     * <p>Тестовый режим используется при передаче параметра production="false" в виртуальную машину.
     * В других случаях - продуктивный режим</p>
     * */
    static boolean isInProductionMode(){
            if("false".equals(System.getProperty("production")))
                return false;
            else
                return true;
    }
    /**
     * <p>Возвращает величину таймаута для разгадки Captcha в секундах.</p>
     * <p>Значение берется из параметра ttl виртуальной машины. Если получить значение из парамтера не удалось,
     * устанавливается значение по умолчанию - 60 с </p>
     * */
    static Integer captchaTimeout(){
        Integer timeout=0;
        try{
            timeout =Integer.parseInt(System.getProperty("ttl"));
        }catch(NumberFormatException e){}
        finally {
            return timeout;
        }

    }
}
