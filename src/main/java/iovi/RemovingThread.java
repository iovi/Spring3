package iovi;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**Поток, выполняющий удаление устаревших данных */
public class RemovingThread extends Thread {
    Map<String,CaptchaWithCreationTime> captchas;
    long timeout;
    long wakeUpPeriod;

    public RemovingThread(Map<String,CaptchaWithCreationTime> captchas,long timeout,long wakeUpPeriod){
        this.captchas=captchas;
        this.timeout=timeout;
        this.wakeUpPeriod=wakeUpPeriod;
    }
    void RemoveOldCaptchas(){
        Iterator<Map.Entry<String,CaptchaWithCreationTime>> iterator;
        for(iterator=captchas.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String,CaptchaWithCreationTime> entry = iterator.next();
            if(entry.getValue().creationTime.getTime()+timeout< new Date().getTime()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void run() {
        for(;;) {
            try {
                Thread.sleep(wakeUpPeriod);
            } catch (InterruptedException e) {
            }
            RemoveOldCaptchas();
        }
    }
}
