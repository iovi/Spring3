package iovi;

import java.util.List;

/** Поток, занимающийся периодическим удалением устаревших объектов*/
public class RemovingThread extends Thread {
    long wakeUpPeriod;
    List<OldObjectsRemover> services;

    /**
     Конструктор. В дальнейшем в ходе работы поток будет каждые wakeUpPeriod мс выполнять
     удаление объектов для всех сервисов services
     * @param wakeUpPeriod период удалениея в мс
     * @param services - список сервисов, умеющих удалять свои устаревшие объекты (реализуют {@link OldObjectsRemover})
     * */
    public RemovingThread(long wakeUpPeriod, List<OldObjectsRemover> services){
        this.wakeUpPeriod=wakeUpPeriod;
        this.services=services;
    }

    @Override
    public void run() {
        for(;;) {
            try {
                Thread.sleep(wakeUpPeriod);
            } catch (InterruptedException e) {}
            for(OldObjectsRemover service:services){
                service.removeOldObjects();
            }
        }
    }
}
