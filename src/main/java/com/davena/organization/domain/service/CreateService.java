package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserRequest;
import com.davena.organization.application.dto.user.UserResponse;
import com.davena.organization.application.dto.ward.*;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.ward.*;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateService {

    private final UserRepository userRepository;
    private final WardRepository wardRepository;
    private final ExistenceService existenceCheck;

    public UserResponse createUser(UserRequest request) {
        User user = User.create(request.name(), request.loginId(), request.password(), request.phoneNumber());
        return UserResponse.from(userRepository.save(user));
    }

    public WardResponse createWard(WardRequest request) {
        Ward ward = Ward.create(
                new HospitalId(request.hospitalId()),
                new UserId(request.supervisorId()),
                request.wardName(),
                createToken()
        );
        return WardResponse.from(wardRepository.save(ward));
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }

    public TeamResponse addNewTeam(TeamRequest request) {
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        existenceCheck.verifySupervisor(ward, new UserId(request.supervisorId()));
        TeamId teamId = ward.addNewTeam(request.name());
        return TeamResponse.from(ward.getId(), teamId, request.name());
    }

    public GradeResponse addNewGrade(GradeRequest request) {
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        existenceCheck.verifySupervisor(ward, new UserId(request.supervisorId()));
        GradeId gradeId = ward.addNewGrade(request.name());
        return GradeResponse.from(ward.getId(), gradeId, request.name());
    }

    public ShiftResponse addNewShift(ShiftRequest request) {
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        existenceCheck.verifySupervisor(ward, new UserId(request.supervisorId()));
        ShiftId shiftId = ward.addNewShift(request.name());
        return ShiftResponse.from(ward.getId(), shiftId, request.name());
    }



}
