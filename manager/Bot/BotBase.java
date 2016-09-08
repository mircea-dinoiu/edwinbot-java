package manager.Bot;

import engine.chatango.manager.StreamManager.StreamManager;
import manager.Cron;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class BotBase extends StreamManager {
    public BotBase(Object... args) throws Exception {
        super(args);
    }

    /**
     * Launches a job from the Cron manager
     *
     * @param jobName job name to launch
     */
    public void launchCronJob(String jobName) {
        Method method;

        try {
            method = Cron.class.getMethod(jobName, Bot.class);

            try {
                method.invoke(Cron.class, this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            System.out.println(String.format("There is no cron job method called '%s'", jobName));
        }
    }

    /**
     * Defers the launch cron job method to a thread
     *
     * @param jobName job name to launch
     */
    public void launchCronThreadedJob(String jobName) {
        new Thread() {
            @Override
            public void run() {
                launchCronJob(jobName);
            }
        }.start();
    }

    /**
     * Cron tick
     * Checks if is the time for some cron activities and eventually launches the jobs
     */
    public void cronTick() {
//        while self._running:
//            if len(self.get_room_names()) < 1:
//            self.stop()
//
//            now = int(time.time())
//
//            # CONTINUOUSLY
//            if time.time() - self.db.get_info('boot_time') > 10:
//                cron.level_lottery(self)
//                cron.coins_lottery(self)
//
//            # 1 HOUR
//            if now - self.db.get_config('cron_1h') > 3600 - 1:
//                self.db.set_config('cron_1h', 3600, 'add')
//
//                # Jobs
//                self.cron_job(cron.unpark)
//
//            # 2 HOURS
//            if now - self.db.get_config('cron_2h') > 3600 * 2 - 1:
//                self.db.set_config('cron_2h', 3600 * 2, 'add')
//
//            # 3 HOURS
//            if now - self.db.get_config('cron_3h') > 3600 * 3 - 1:
//                self.db.set_config('cron_3h', 3600 * 3, 'add')
//
//                # Jobs
//                self.cron_job(cron.bank_interest)
//
//            # 6 HOURS
//            if now - self.db.get_config('cron_6h') > 3600 * 6 - 1:
//                self.db.set_config('cron_6h', 3600 * 6, 'add')
//
//            # 12 HOURS
//            if now - self.db.get_config('cron_12h') > 3600 * 12 - 1:
//                self.db.set_config('cron_12h', 3600 * 12, 'add')
//
//            # 24 HOURS
//            if now - self.db.get_config('cron_24h') > 3600 * 24 - 1:
//                self.db.set_config('cron_24h', 3600 * 24, 'add')
//
//                # Jobs
//                self.cron_job(cron.bank_earnings)
//                self.cron_job(cron.level_downgrade)
    }
}
