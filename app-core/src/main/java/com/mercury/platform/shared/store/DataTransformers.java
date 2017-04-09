package com.mercury.platform.shared.store;

import com.mercury.platform.core.AppStarter;
import com.mercury.platform.core.misc.SoundType;
import com.mercury.platform.core.misc.WhisperNotifierStatus;
import com.mercury.platform.shared.ConfigManager;
import com.mercury.platform.shared.FrameVisibleState;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.service.ConfigurationService;
import com.mercury.platform.shared.entity.SoundDescriptor;
import rx.Observable;

import java.util.Random;


public class DataTransformers {
    private static final ConfigurationService<SoundDescriptor, String> soundService = Configuration.get().soundConfiguration();
    public static Observable.Transformer<SoundType, SoundDescriptor> transformSoundData() {
        String[] clicks = {
                "app/sounds/click1/button-pressed-10.wav",
                "app/sounds/click1/button-pressed-20.wav",
                "app/sounds/click1/button-pressed-30.wav"};
        return obs -> obs.map(soundType -> {
            SoundDescriptor descriptor = new SoundDescriptor();
            switch (soundType){
                case MESSAGE:{
                    SoundDescriptor desc = soundService.get("notification");
                    WhisperNotifierStatus status = ConfigManager.INSTANCE.getWhisperNotifier();
                    if (status == WhisperNotifierStatus.ALWAYS ||
                            ((status == WhisperNotifierStatus.ALTAB) && (AppStarter.APP_STATUS == FrameVisibleState.HIDE))) {
                        return desc;
                    }
                    return new SoundDescriptor(desc.getWavPath(),-80f);
                }
                case CHAT_SCANNER: {
                    return soundService.get("chat_scanner");
                }
                case CLICKS: {
                    descriptor.setWavPath(clicks[new Random().nextInt(3)]);
                    descriptor.setDb(soundService.get("clicks").getDb());
                    break;
                }
                case UPDATE: {
                    descriptor.setWavPath("app/patch_tone.wav");
                    descriptor.setDb(soundService.get("update").getDb());
                    break;
                }
            }
            return descriptor;
        });
    }
}
