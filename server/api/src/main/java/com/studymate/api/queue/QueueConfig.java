package com.studymate.api.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Bean
    public Queue settingQueue() {
        return new Queue("settingQueue");
    }
    @Bean
    public Queue measureQueue() {
        return new Queue("measureQueue");
    }
    @Bean
    public Queue studyTimeQueue() {
        return new Queue("studyTimeQueue");
    }
    @Bean
    public Queue studyRecordQueue() {
        return new Queue("studyRecordQueue");
    }
    @Bean
    public Queue chattingQueue() {
        return new Queue("chattingQueue");
    }

    @Bean
    public Binding bindSetting() {
        return new Binding("settingQueue"
                ,Binding.DestinationType.QUEUE
                ,"amq.topic"
                ,"setting"
                , null);
    }
    @Bean
    public Binding bindMeasure() {
        return new Binding("measureQueue"
                ,Binding.DestinationType.QUEUE
                ,"amq.topic"
                ,"measure_data"
                , null);
    }
    @Bean
    public Binding bindStudyTime() {
        return new Binding("studyTimeQueue"
                ,Binding.DestinationType.QUEUE
                ,"amq.topic"
                ,"study_time"
                , null);
    }
    @Bean
    public Binding bindStudyRecord() {
        return new Binding("studyRecordQueue"
                ,Binding.DestinationType.QUEUE
                ,"amq.topic"
                ,"study_record"
                , null);
    }
    @Bean
    public Binding bindChatting() {
        return new Binding("chattingQueue"
                ,Binding.DestinationType.QUEUE
                ,"amq.topic"
                ,"chatting_data"
                , null);
    }
}
