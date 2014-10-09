package org.ovirt.engine.core.vdsbroker.vdsbroker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.ovirt.engine.core.common.businessentities.LUNs;
import org.ovirt.engine.core.common.businessentities.StorageType;
import org.ovirt.engine.core.utils.RandomUtils;
import org.ovirt.engine.core.utils.RandomUtilsSeedingRule;
import org.ovirt.engine.core.vdsbroker.xmlrpc.XmlRpcStruct;

public class GetDeviceListVDSCommandTest {

    @Rule
    public static RandomUtilsSeedingRule rusr = new RandomUtilsSeedingRule();

    @Test
    public void parseLunFromXmlRpcReturnsIscsiByDefault() throws Exception {
        testParseLunFromXmlRpcForDevtypeField(StorageType.ISCSI, "");
    }

    @Test
    public void parseLunFromXmlRpcReturnsUnknownForFcp() throws Exception {
        testParseLunFromXmlRpcForDevtypeField(StorageType.UNKNOWN, GetDeviceListVDSCommand.DEVTYPE_VALUE_FCP);
    }

    /**
     * Test that ParseLunFromXmlRpc parses the {@link GetDeviceListVDSCommand#DEVTYPE_FIELD} correctly.
     *
     * @param expectedStorageType
     *            The storage type expected to return.
     * @param mockDevtype
     *            The value that the XML RPC will hold.
     */
    private static void testParseLunFromXmlRpcForDevtypeField(StorageType expectedStorageType, String mockDevtype) {
        XmlRpcStruct xlun = new XmlRpcStruct();
        xlun.add(GetDeviceListVDSCommand.DEVTYPE_FIELD, mockDevtype);

        LUNs lun = GetDeviceListVDSCommand.ParseLunFromXmlRpc(xlun);

        assertEquals(expectedStorageType, lun.getLunType());
    }

    @Test
    public void parseLunFromXmlRpcReturnsUnknownForNoField() throws Exception {
        XmlRpcStruct xlun = new XmlRpcStruct();
        LUNs lun = GetDeviceListVDSCommand.ParseLunFromXmlRpc(xlun);

        assertEquals(StorageType.UNKNOWN, lun.getLunType());
    }

    @Test
    public void parseLunPathStatusFromXmlRpc() throws Exception {
        int numActivePaths = 1 + RandomUtils.instance().nextInt(3);
        int numNonActivePaths = 2 + RandomUtils.instance().nextInt(3);
        int numPaths = numActivePaths + numNonActivePaths;

        // Randomize some devices
        String physicalDevicePrefix = "physical";
        List<XmlRpcStruct> paths = new ArrayList<XmlRpcStruct>(numPaths);
        for (int i = 0; i < numPaths; ++i) {
            XmlRpcStruct path = new XmlRpcStruct();
            path.add(GetDeviceListVDSCommand.LUN_FIELD, String.valueOf(i));
            path.add(GetDeviceListVDSCommand.PHYSICAL_DEVICE_FIELD, physicalDevicePrefix + i);
            path.add(GetDeviceListVDSCommand.DEVICE_STATE_FIELD,
                    i < numActivePaths ?
                            GetDeviceListVDSCommand.DEVICE_ACTIVE_VALUE : RandomUtils.instance().nextString(10));
            paths.add(path);
        }

        XmlRpcStruct xlun = new XmlRpcStruct();
        xlun.add(GetDeviceListVDSCommand.PATHSTATUS, paths.toArray(new XmlRpcStruct[paths.size()]));

        // Parse the XmlRpc
        LUNs lun = GetDeviceListVDSCommand.ParseLunFromXmlRpc(xlun);

        // Go over the directory
        assertEquals("wrong number of paths", numPaths, lun.getPathCount());
        Map<String, Boolean> pathDir = new HashMap<String, Boolean>(lun.getPathsDictionary());
        for (int i = 0; i < numPaths; ++i) {
            // Assert for each device
            String device = physicalDevicePrefix + i;
            Boolean isActive = pathDir.remove(device);

            assertNotNull("Device " + device + " isn't in the device map", isActive);
            assertEquals("Device " + device + " has the wrong state ", i < numActivePaths, isActive);
        }

        // After remove all the expected devices, the directory should be empty
        assertTrue("Wrong devices in the device map", pathDir.isEmpty());
    }
}
