/**
 * Oshi (https://github.com/dblock/oshi)
 * 
 * Copyright (c) 2010 - 2016 The Oshi Project Team
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * dblock[at]dblock[dot]org
 * alessandro[at]perucchi[dot]org
 * widdis[at]gmail[dot]com
 * https://github.com/dblock/oshi/graphs/contributors
 */
package oshi.jna.platform.mac;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import oshi.jna.platform.mac.CoreFoundation.CFArrayRef;
import oshi.jna.platform.mac.CoreFoundation.CFDictionaryRef;
import oshi.jna.platform.mac.CoreFoundation.CFMutableDictionaryRef;
import oshi.jna.platform.mac.CoreFoundation.CFStringRef;
import oshi.jna.platform.mac.CoreFoundation.CFTypeRef;

/**
 * Power Supply stats. This class should be considered non-API as it may be
 * removed if/when its code is incorporated into the JNA project.
 * 
 * @author widdis[at]gmail[dot]com
 */
public interface IOKit extends Library {
    IOKit INSTANCE = (IOKit) Native.loadLibrary("IOKit", IOKit.class);

    static final CFStringRef IOPS_NAME_KEY = CFStringRef.toCFString("Name");

    static final CFStringRef IOPS_IS_PRESENT_KEY = CFStringRef.toCFString("Is Present");

    static final CFStringRef IOPS_CURRENT_CAPACITY_KEY = CFStringRef.toCFString("Current Capacity");

    static final CFStringRef IOPS_MAX_CAPACITY_KEY = CFStringRef.toCFString("Max Capacity");

    static final String SMC_KEY_FAN_NUM = "FNum";

    static final String SMC_KEY_FAN_SPEED = "F%dAc";

    static final String SMC_KEY_CPU_TEMP = "TC0P";

    static final String SMC_KEY_CPU_VOLTAGE = "VC0C";

    static final byte SMC_CMD_READ_BYTES = 5;

    static final byte SMC_CMD_READ_KEYINFO = 9;

    static final int KERNEL_INDEX_SMC = 2;

    static final byte[] DATATYPE_SP78 = { (byte) 's', (byte) 'p', (byte) '7', (byte) '8', 0 };

    /**
     * Holds the return value of SMC version query.
     */
    static class SMCKeyDataVers extends Structure {
        public byte major;
        public byte minor;
        public byte build;
        public byte[] reserved = new byte[1];
        public short release;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "major", "minor", "build", "reserved", "release" });
        }
    }

    /**
     * Holds the return value of SMC pLimit query.
     */
    static class SMCKeyDataPLimitData extends Structure {
        public short version;
        public short length;
        public int cpuPLimit;
        public int gpuPLimit;
        public int memPLimit;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "version", "length", "cpuPLimit", "gpuPLimit", "memPLimit" });
        }
    }

    /**
     * Holds the return value of SMC KeyInfo query.
     */
    static class SMCKeyDataKeyInfo extends Structure {
        public int dataSize;
        public int dataType;
        public byte dataAttributes;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "dataSize", "dataType", "dataAttributes" });
        }
    }

    /**
     * Holds the return value of SMC query.
     */
    static class SMCKeyData extends Structure {
        public int key;
        public SMCKeyDataVers vers;
        public SMCKeyDataPLimitData pLimitData;
        public SMCKeyDataKeyInfo keyInfo;
        public byte result;
        public byte status;
        public byte data8;
        public int data32;
        public byte[] bytes = new byte[32];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "key", "vers", "pLimitData", "keyInfo", "result", "status", "data8",
                    "data32", "bytes" });
        }
    }

    /**
     * Holds an SMC value
     */
    static class SMCVal extends Structure {
        public byte[] key = new byte[5];
        public int dataSize;
        public byte[] dataType = new byte[5];
        public byte[] bytes = new byte[32];

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[] { "key", "dataSize", "dataType", "bytes" });
        }
    }

    class IOConnect extends IntByReference {
    }

    class MachPort extends IntByReference {
    }

    CFTypeRef IOPSCopyPowerSourcesInfo();

    CFArrayRef IOPSCopyPowerSourcesList(CFTypeRef blob);

    CFDictionaryRef IOPSGetPowerSourceDescription(CFTypeRef blob, CFTypeRef ps);

    double IOPSGetTimeRemainingEstimate();

    int IOMasterPort(int unused, IntByReference masterPort);

    CFMutableDictionaryRef IOServiceMatching(String name);

    int IOServiceGetMatchingService(int port, CFMutableDictionaryRef matchingDictionary);

    int IOServiceGetMatchingServices(int port, CFMutableDictionaryRef matchingDictionary, IntByReference iterator);

    int IOServiceOpen(int service, int owningTask, int type, IntByReference connect);

    int IOServiceClose(int connect);

    void IOObjectRelease(int object);

    // Requires OS X 10.5+
    int IOConnectCallStructMethod(int connection, int selector, Structure inputStructure, int structureInputSize,
            Structure outputStructure, IntByReference structureOutputSize);
}