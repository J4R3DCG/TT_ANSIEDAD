package anxiety.detector.api.service.application.ports.output;

import anxiety.detector.api.service.domain.model.AnxietyResult;

public interface PrototypeCommunicationOutputPort {
  AnxietyResult receiveAnxietyResult();
}
