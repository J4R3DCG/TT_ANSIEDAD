package anxiety.detector.api.service.application.ports.output;

import anxiety.detector.api.service.domain.model.AnxietyResult;

public interface SendVideoOutputPort {
  AnxietyResult sendVideoToPrototype(byte[] data, String fileName);
}