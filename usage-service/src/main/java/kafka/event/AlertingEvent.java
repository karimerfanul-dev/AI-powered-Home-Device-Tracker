package kafka.event;

import lombok.Builder;

@Builder
public record AlertingEvent(Long userId,
                            String message,
                            double threshold,
                            double energyConsumption,
                            String email
                            ) {

}
