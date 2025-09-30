package anxiety.detector.api.service.application.ports.input;

import anxiety.detector.api.service.domain.model.UserRequest;

public interface ProcessVideoInputPort {
    void execute(UserRequest request);
}