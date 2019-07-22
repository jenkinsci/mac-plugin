//package fr.jenkins.plugins.mac.strategy;
//
//import static hudson.slaves.NodeProvisioner.StrategyDecision.CONSULT_REMAINING_STRATEGIES;
//import static hudson.slaves.NodeProvisioner.StrategyDecision.PROVISIONING_COMPLETED;
//
//import javax.annotation.Nonnull;
//
//import fr.jenkins.plugins.mac.MacCloud;
//import groovy.util.logging.Slf4j;
//import hudson.Extension;
//import hudson.model.Label;
//import hudson.model.LoadStatistics;
//import hudson.model.Queue;
//import hudson.model.queue.QueueListener;
//import hudson.slaves.Cloud;
//import hudson.slaves.NodeProvisioner;
//import hudson.slaves.NodeProvisioner.Strategy;
//import hudson.slaves.NodeProvisioner.StrategyDecision;
//import jenkins.model.Jenkins;
//
///**
// * Based on https://github.com/jenkinsci/one-shot-executor-plugin/blob/master/src/main/java/org/jenkinsci/plugins/oneshot/OneShotProvisionerStrategy.java
// *
// * @author Mathieu DELROCQ
// */
//@Extension
//@Slf4j
//public class OneShotNodeProvisionerStrategy extends Strategy {
//
//    @Nonnull
//    @Override
//    public StrategyDecision apply(@Nonnull NodeProvisioner.StrategyState state) {
//        if (Jenkins.get().isQuietingDown()) {
//            return CONSULT_REMAINING_STRATEGIES
//        }
//
//        for (Cloud cloud : Jenkins.get().clouds) {
//            if (cloud instanceof MacCloud) {
//                final StrategyDecision decision = applyFoCloud(state, (MacCloud) cloud);
//                if (decision == PROVISIONING_COMPLETED) return decision
//            }
//        }
//        return CONSULT_REMAINING_STRATEGIES
//    }
//
//    private StrategyDecision applyFoCloud(@Nonnull NodeProvisioner.StrategyState state, MacCloud cloud) {
//
//        final Label label = state.getLabel()
//
//        if (!cloud.canProvision(label)) {
//            return CONSULT_REMAINING_STRATEGIES
//        }
//
//        LoadStatistics.LoadStatisticsSnapshot snapshot = state.getSnapshot()
//        log.info("Available executors={}, connecting={}, planned={}",
//                snapshot.getAvailableExecutors(), snapshot.getConnectingExecutors(), state.getPlannedCapacitySnapshot());
//        int availableCapacity =
//                snapshot.getAvailableExecutors()
//        + snapshot.getConnectingExecutors()
//        + state.getPlannedCapacitySnapshot()
//
//        int currentDemand = snapshot.getQueueLength()
//        log.info("Available capacity={}, currentDemand={}",
//                availableCapacity, currentDemand)
//
//        if (availableCapacity < currentDemand) {
//            Collection<NodeProvisioner.PlannedNode> plannedNodes = cloud.provision(label, currentDemand - availableCapacity);
//            log.info("Planned {} new nodes", plannedNodes.size());
//            state.recordPendingLaunches(plannedNodes)
//            availableCapacity += plannedNodes.size()
//            log.info("After provisioning, available capacity={}, currentDemand={}",
//                    availableCapacity, currentDemand)
//        }
//
//        if (availableCapacity >= currentDemand) {
//            log.info("Provisioning completed")
//            return PROVISIONING_COMPLETED
//        } else {
//            log.info("Provisioning not complete, consulting remaining strategies");
//            return CONSULT_REMAINING_STRATEGIES
//        }
//    }
//
//    /**
//     * Ping the nodeProvisioner as a new task enters the queue, so it can provision a MacSlave without delay.
//     */
//    @Extension
//    public static class FastProvisionning extends QueueListener {
//
//        @Override
//        public void onEnterBuildable(Queue.BuildableItem item) {
//            final Jenkins jenkins = Jenkins.get()
//            final Label label = item.getAssignedLabel()
//            for (Cloud cloud : jenkins.clouds) {
//                if (cloud instanceof MacCloud && cloud.canProvision(label)) {
//                    final NodeProvisioner provisioner = (label == null
//                            ? jenkins.unlabeledNodeProvisioner
//                            : label.nodeProvisioner)
//                    provisioner.suggestReviewNow()
//                }
//            }
//        }
//    }
//}
