package org.ovirt.engine.core.bll.validator.storage;

import com.woorea.openstack.base.client.OpenStackResponseException;
import org.ovirt.engine.core.bll.ValidationResult;
import org.ovirt.engine.core.bll.provider.storage.OpenStackVolumeProviderProxy;
import org.ovirt.engine.core.common.businessentities.storage.CinderDisk;
import org.ovirt.engine.core.common.errors.VdcBllMessages;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;
import org.ovirt.engine.core.dao.DiskDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class CinderDisksValidator {

    private Iterable<CinderDisk> cinderDisks;

    private Map<Guid, OpenStackVolumeProviderProxy> diskProxyMap;

    public CinderDisksValidator(Iterable<CinderDisk> cinderDisks) {
        this.cinderDisks = cinderDisks;
        this.diskProxyMap = initializeVolumeProviderProxyMap();
    }

    public CinderDisksValidator(CinderDisk cinderDisk) {
        this(Collections.singleton(cinderDisk));
    }

    private ValidationResult validate(Callable<ValidationResult> callable) {
        try {
            return callable.call();
        } catch (OpenStackResponseException e) {
            return new ValidationResult(VdcBllMessages.ACTION_TYPE_FAILED_CINDER,
                    String.format("$cinderException %1$s", e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Guid, OpenStackVolumeProviderProxy> initializeVolumeProviderProxyMap() {
        if (diskProxyMap == null) {
            diskProxyMap = new HashMap<>();
            for (CinderDisk cinderDisk : cinderDisks) {
                OpenStackVolumeProviderProxy volumeProviderProxy = getVolumeProviderProxy(cinderDisk);
                diskProxyMap.put(cinderDisk.getId(), volumeProviderProxy);
            }

        }
        return diskProxyMap;
    }

    private OpenStackVolumeProviderProxy getVolumeProviderProxy(CinderDisk cinderDisk) {
        if (cinderDisk == null || cinderDisk.getStorageIds().isEmpty()) {
            return null;
        }
        return OpenStackVolumeProviderProxy.getFromStorageDomainId(cinderDisk.getStorageIds().get(0));
    }

    protected DiskDao getDiskDao() {
        return DbFacade.getInstance().getDiskDao();
    }
}
