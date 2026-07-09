package com.dvid.dcam.feature.auth.application.usecase;

import com.dvid.dcam.feature.auth.domain.OperatorAccount;
import com.dvid.dcam.feature.auth.domain.UserProvisioningRequest;
import com.dvid.dcam.feature.auth.domain.UserProvisioningResult;
import java.util.List;

public interface ManageOperatorUsersUseCase {
    void ensureDefaultUser();
    UserProvisioningResult upsert(UserProvisioningRequest request);
    List<OperatorAccount> users();
}
