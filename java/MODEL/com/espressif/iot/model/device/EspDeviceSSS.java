package com.espressif.iot.model.device;

import java.util.ArrayList;
import java.util.List;

import com.espressif.iot.adt.tree.IEspDeviceTreeElement;
import com.espressif.iot.device.IEspDevice;
import com.espressif.iot.device.IEspDeviceConfigure;
import com.espressif.iot.device.IEspDeviceSSS;
import com.espressif.iot.device.builder.BEspDeviceConfigure;
import com.espressif.iot.type.device.EspDeviceType;
import com.espressif.iot.type.device.IEspDeviceStatus;
import com.espressif.iot.type.device.state.EspDeviceState;
import com.espressif.iot.type.device.status.EspStatusLight;
import com.espressif.iot.type.device.status.EspStatusPlug;
import com.espressif.iot.type.device.status.EspStatusPlugs;
import com.espressif.iot.type.device.status.EspStatusRemote;
import com.espressif.iot.type.net.IOTAddress;
import com.espressif.iot.user.builder.BEspUser;
import com.espressif.iot.util.RandomUtil;

/**
 * Support SoftAP and Station device
 * 
 */
public class EspDeviceSSS extends EspDevice implements IEspDeviceSSS
{
    private IOTAddress mIOTAddress;
    
    private IEspDeviceStatus mStatus;
    
    // -1, -2, ... is used to activate softap device by direct connect,
    // 1, 2, ... is used by server
    private static long mIdCreator = -Long.MAX_VALUE / 2;
    
    private synchronized long getNextId()
    {
        return --mIdCreator;
    }
    
    public EspDeviceSSS(IOTAddress iotAddress)
    {
        setKey(RandomUtil.randomString(20));
        init(iotAddress);
    }
    
    private void init(IOTAddress iotAddress)
    {
        mIOTAddress = iotAddress;
        
        EspDeviceState stateLocal = new EspDeviceState();
        stateLocal.addStateLocal();
        setDeviceState(stateLocal);
        setName(mIOTAddress.getSSID());
        setBssid(mIOTAddress.getBSSID());
        setInetAddress(mIOTAddress.getInetAddress());
        setDeviceType(mIOTAddress.getDeviceTypeEnum());
        setRouter(mIOTAddress.getRouter());
        setRootDeviceBssid(mIOTAddress.getRootBssid());
        setIsMeshDevice(iotAddress.isMeshDevice());
        setId(getNextId());
        
        EspDeviceType deviceType = mIOTAddress.getDeviceTypeEnum();
        if (deviceType != null)
        {
            switch (deviceType)
            {
                case LIGHT:
                    mStatus = new EspStatusLight();
                    break;
                case PLUG:
                    mStatus = new EspStatusPlug();
                    break;
                case REMOTE:
                    mStatus = new EspStatusRemote();
                    break;
                case PLUGS:
                    mStatus = new EspStatusPlugs();
                    break;
                case ROOT:
                    break;
                case FLAMMABLE:
                    break;
                case HUMITURE:
                    break;
                case VOLTAGE:
                    break;
                case NEW:
                    break;
            }
        }
    }
    
    @Override
    public void setIOTAddress(IOTAddress iotAddress)
    {
        mIOTAddress = iotAddress;
        
        init(iotAddress);
    }
    
    @Override
    public IOTAddress getIOTAddress()
    {
        return mIOTAddress;
    }
    
    @Override
    public IEspDeviceStatus getDeviceStatus()
    {
        return mStatus;
    }
    
    @Override
    public List<IEspDeviceTreeElement> getDeviceTreeElementList()
    {
        List<IEspDevice> devices = new ArrayList<IEspDevice>();
        devices.add(this);
        devices.addAll(BEspUser.getBuilder().getInstance().getStaDeviceList());
        return getDeviceTreeElementList(devices);
    }
    
    @Override
    public IEspDeviceConfigure createConfiguringDevice(String random40)
    {
        IEspDeviceConfigure device = BEspDeviceConfigure.getInstance().alloc(this.mBssid, random40);
        device.setInetAddress(mInetAddress);
        device.setIsMeshDevice(mIsMeshDevice);
        device.setRouter(mRouter);
        device.setRootDeviceBssid(mRootDeviceBssid);
        device.setName(mDeviceName);
        return device;
    }
    
}
