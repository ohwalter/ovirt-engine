package org.ovirt.engine.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.ovirt.engine.core.common.businessentities.VmBase;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.common.businessentities.storage.UnregisteredDisk;
import org.ovirt.engine.core.common.businessentities.storage.UnregisteredDiskId;
import org.ovirt.engine.core.compat.Guid;
import org.springframework.util.StringUtils;

public class UnregisteredDisksDaoTest extends BaseDaoTestCase<UnregisteredDisksDao> {
    @Test
    public void testGetWithDiskId() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
        assertTrue("Vms id should be empty list in the UnregisteredDisks table",
                unregisteredDisk.get(0).getVms().isEmpty());
    }

    @Test
    public void testGetWithNotExistingDiskId() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(Guid.newGuid(), FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should not exists in the UnregisteredDisks table", unregisteredDisk.isEmpty());
    }

    @Test
    public void testGetDiskAttachedToVm() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK2, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
        assertEquals("Disk should have one vm attached", 1, unregisteredDisk.get(0).getVms().size());
    }

    @Test
    public void testGetDiskAttachedToMultipleVms() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK3, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
        assertEquals("Disk should be attached to VM", 1, unregisteredDisk.get(0).getVms().size());
    }

    @Test
    public void testGetDiskAttachedToMultipleVmsWithoutDescription() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK4, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
        assertEquals("Disk should be attached to VM", 1, unregisteredDisk.get(0).getVms().size());
        assertFalse("Disk should have disk alias", unregisteredDisk.get(0).getDiskImage().getDiskAlias().isEmpty());
        assertTrue("Disk should have an empty disk description",
                StringUtils.isEmpty(unregisteredDisk.get(0).getDiskImage().getDiskDescription()));
    }

    @Test
    public void testGetDiskAttachedToMultipleVmsWithoutAliasAndDescription() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK5, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
        assertEquals("Disk should be attached to VM", 1, unregisteredDisk.get(0).getVms().size());
        assertTrue("Disk should have an empty disk alias", unregisteredDisk.get(0)
                .getDiskAlias()
                .isEmpty());
        assertTrue("Disk should have an empty disk description",
                StringUtils.isEmpty(unregisteredDisk.get(0).getDiskDescription()));
    }

    @Test
    public void testGetDiskForAllStorageDomain() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK, null);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
        assertTrue("Disk should exists in the UnregisteredDisks table", unregisteredDisk.get(0).getVms().isEmpty());
    }

    @Test
    public void testGetAllDisksForStorageDomain() {
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(null, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !unregisteredDisk.isEmpty());
    }

    @Test
    public void testDeleteUnregisteredDiskRelatedToVM() {
        List<UnregisteredDisk> unregisteredDisk2 =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK2, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        List<UnregisteredDisk> unregisteredDisk3 =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK3, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table.", !unregisteredDisk2.isEmpty());
        assertTrue("Disk should exists in the UnregisteredDisks table.", !unregisteredDisk3.isEmpty());
        dao.removeUnregisteredDiskRelatedToVM(FixturesTool.VM_RHEL5_POOL_57, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        unregisteredDisk2 =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK2, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        unregisteredDisk3 =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK3, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table.", unregisteredDisk2.isEmpty());
        assertTrue("Disk should exists in the UnregisteredDisks table.", unregisteredDisk3.isEmpty());
    }

    @Test
    public void testRemoveUnregisteredDiskRelatedToVM() {
        dao.removeUnregisteredDisk(FixturesTool.UNREGISTERED_DISK, FixturesTool.STORAGE_DOMAIN_NFS2_1);
        List<UnregisteredDisk> unregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(FixturesTool.UNREGISTERED_DISK, null);
        assertTrue("Disk should not exists in the UnregisteredDisks table after being deleted.",
                unregisteredDisk.isEmpty());
    }

    @Test
    public void testSaveDiskWithAliasAndDescription() {
        ArrayList<VmBase> vms = new ArrayList<>();
        UnregisteredDisk unregisteredDisk = initUnregisteredDisks(vms);
        dao.saveUnregisteredDisk(unregisteredDisk);
        List<UnregisteredDisk> fetchedUnregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(unregisteredDisk.getDiskId(),
                        FixturesTool.STORAGE_DOMAIN_NFS2_1);

        assertTrue("Disk should exists in the UnregisteredDisks table", !fetchedUnregisteredDisk.isEmpty());
        assertTrue("Disk should not have multiple vms attached", fetchedUnregisteredDisk.get(0).getVms().isEmpty());
        assertEquals("Disk alias should be the same as initialized", "Disk Alias", fetchedUnregisteredDisk.get(0).getDiskAlias());
        assertEquals("Disk description should be the same as initialized", "Disk Description", fetchedUnregisteredDisk.get(0).getDiskDescription());
        assertEquals("Storage Domain id should be the same as initialized", 0, fetchedUnregisteredDisk.get(0)
                .getStorageDomainId()
                .compareTo(FixturesTool.STORAGE_DOMAIN_NFS2_1));
    }

    private UnregisteredDisk initUnregisteredDisks(ArrayList<VmBase> vms) {
        DiskImage diskImage = new DiskImage();
        diskImage.setId(Guid.newGuid());
        diskImage.setDiskAlias("Disk Alias");
        diskImage.setDiskDescription("Disk Description");
        diskImage.setStorageIds(new ArrayList<>(Collections.singletonList(FixturesTool.STORAGE_DOMAIN_NFS2_1)));
        UnregisteredDiskId id = new UnregisteredDiskId(diskImage.getId(), diskImage.getStorageIds().get(0));
        return new UnregisteredDisk(id, diskImage, vms);
    }

    @Test
    public void testSaveAttachedDiskWithoutAliasAndDescription() {
        VmBase vm1 = new VmBase();
        vm1.setId(Guid.newGuid());
        vm1.setName("First VM");
        ArrayList<VmBase> vms = new ArrayList<>();
        vms.add(vm1);

        // Set new disk image.
        DiskImage diskImage = new DiskImage();
        diskImage.setId(Guid.newGuid());
        diskImage.setStorageIds(new ArrayList<>(Collections.singletonList(FixturesTool.STORAGE_DOMAIN_NFS2_1)));
        UnregisteredDiskId id = new UnregisteredDiskId(diskImage.getId(), diskImage.getStorageIds().get(0));
        UnregisteredDisk unregDisk = new UnregisteredDisk(id, diskImage, vms);
        dao.saveUnregisteredDisk(unregDisk);
        List<UnregisteredDisk> fetchedUnregisteredDisk =
                dao.getByDiskIdAndStorageDomainId(unregDisk.getDiskId(), FixturesTool.STORAGE_DOMAIN_NFS2_1);
        assertTrue("Disk should exists in the UnregisteredDisks table", !fetchedUnregisteredDisk.isEmpty());
        assertEquals("Disk should have vm attached", 1, fetchedUnregisteredDisk.get(0).getVms().size());
        assertTrue("Disk alias should not be initialized",
                fetchedUnregisteredDisk.get(0).getDiskAlias().isEmpty());
        assertTrue("Disk description should not be initialized",
                StringUtils.isEmpty(fetchedUnregisteredDisk.get(0).getDiskDescription()));
        assertEquals("Storage Domain id should be the same as initialized", 0, fetchedUnregisteredDisk.get(0)
                .getStorageDomainId()
                .compareTo(FixturesTool.STORAGE_DOMAIN_NFS2_1));
    }
}
