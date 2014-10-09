package org.ovirt.engine.core.vdsbroker.gluster;

import org.ovirt.engine.core.common.vdscommands.gluster.GlusterVolumeProfileInfoVDSParameters;
import org.ovirt.engine.core.vdsbroker.irsbroker.GlusterVolumeProfileInfoReturnForXmlRpc;
import org.ovirt.engine.core.vdsbroker.vdsbroker.StatusForXmlRpc;

public class GetGlusterVolumeProfileInfoVDSCommand<P extends GlusterVolumeProfileInfoVDSParameters> extends AbstractGlusterBrokerCommand<P> {
    private GlusterVolumeProfileInfoReturnForXmlRpc result;

    public GetGlusterVolumeProfileInfoVDSCommand(P parameters) {
        super(parameters);
    }

    @Override
    protected StatusForXmlRpc getReturnStatus() {
        return result.getStatus();
    }

    @Override
    protected void ExecuteVdsBrokerCommand() {
        result = getBroker().glusterVolumeProfileInfo(getParameters().getClusterId(), getParameters().getVolumeName());
        ProceedProxyReturnValue();
        setReturnValue(result.getGlusterVolumeProfileInfo());
    }
}
