// AIDLWifiInterface.aidl
package meekuxpjxroject;

// Declare any non-default types here with import statements

interface AIDLWifiInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void bSendtoServer(in byte[] buff);
      void bSendUdptoServer(in byte[] buff);
      String getRemoteConnectState();
            //void bStartTcpClient(inout Socket socket, String ip,boolean needConnect);
      void setWholeLightness(int num);
      void setVolumeness(int num);
      void sendFilesListToDevice(String filesListName, int beginIndex);
      void stopCurPlaying();
      void pauseCurPlaying();
      void playCurPause();
      void sendLightDataTime(in byte[] data, int time);
      void sendNightLightToDevice();
      String getString();
      void sendLightCtrl(int nProgressRed, int nProgressGreen,
            				int nProgressBlue, int nProgressWhite);
      void sendFrame(byte bCmd1, byte bCmd2, in byte[] data);
      void sendFileToDevice(String fileName);
      void sendLightCtrlTime(int nProgressRed, int nProgressGreen,
             				int nProgressBlue, int nProgressWhite,int time);
      void sendLoopFileToDevice(String fileName);
      void sendOffCmd(in byte[] cmd,in  byte[] data, String sn);
      void sendLightC(int nProgressRed, int nProgressGreen,
                      				int nProgressBlue, int nProgressWhite,String sn);
           void setWholeLight(int num ,String sn);
            void sendLight(int num,int nProgressRed, int nProgressGreen,
                                 				int nProgressBlue, int nProgressWhite,String sn);
    boolean creatMediaPlayer(String path);
    boolean start(String path);
    boolean pause(String path);
    boolean stop(String path);
    void sendMusic(in byte[] data, int time,String name);

}
