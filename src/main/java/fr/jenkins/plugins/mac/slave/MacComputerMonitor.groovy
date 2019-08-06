// TODO : Monitor
//package fr.jenkins.plugins.mac.slave
//
//import fr.jenkins.plugins.mac.MacHost
//
//@Singleton
//class MacComputerMonitor {
//
//    Map<String, MacMonitor> macMonitorMap
//    
//    Map<String, MacHost> macHostByUsersMap = new HashMap()
//
//
//    boolean canProvision(String label) {
//        MacMonitor macMonitor = macMonitorMap.get(label)
//        if(null == macMonitor) macMonitor = monitorLabel(label)
//        return macMonitor.workload < macMonitor.nbProvision && macMonitor.workload < macMonitor.nbConnected
//    }
//
//    MacMonitor monitorLabel(String label) {
//        if(macMonitorMap.get(label) == null) {
//            return macMonitorMap.put(label, new MacMonitor())
//        }
//        return macMonitorMap.get(label)
//    }
//
//    private class MacMonitor {
//        int nbProvision = 0
//        int nbConnected = 0
//        int nbUser = 0
//        int workload = 0
//    }
//}
