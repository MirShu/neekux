// AIDLPlayLightSongListService.aidl
package meekuxpjxroject;

// Declare any non-default types here with import statements

interface AIDLPlayLightSongListService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        boolean getLoopMode();
        int getPlayingSongLightId();
        void sendFilesListToDevice(String filesListName);
        void sendLoopFileToDevice(String fileName);
        void stopMp3();

        void createMp3Data(String filepath, boolean hasMusic,String name,int flag,int position);

        boolean createMediaPlayer(String fileName);
        boolean startMediaPlaye(String fileName);
        boolean pause(String fileName);
        boolean stop(String fileName);

}
