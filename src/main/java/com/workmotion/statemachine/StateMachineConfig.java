/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workmotion.statemachine;


import static com.workmotion.statemachine.EmpEvents.ACTIVATE;
import static com.workmotion.statemachine.EmpEvents.BEGIN_CHECK;
import static com.workmotion.statemachine.EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK;
import static com.workmotion.statemachine.EmpEvents.FINISH_SECURITY_CHECK;
import static com.workmotion.statemachine.EmpEvents.FINISH_WORK_PERMIT_CHECK;
import static com.workmotion.statemachine.EmployeeState.ACTIVE;
import static com.workmotion.statemachine.EmployeeState.ADDED;
import static com.workmotion.statemachine.EmployeeState.ALL_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.APPROVED;
import static com.workmotion.statemachine.EmployeeState.IN_CHECK;
import static com.workmotion.statemachine.EmployeeState.SECURITY_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.SECURITY_CHECK_STARTED;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_STARTED;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.data.RepositoryState;
import org.springframework.statemachine.data.RepositoryStateMachineModelFactory;
import org.springframework.statemachine.data.RepositoryTransition;
import org.springframework.statemachine.data.StateRepository;
import org.springframework.statemachine.data.TransitionRepository;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableStateMachine
public class StateMachineConfig {
	
	@PostConstruct
	public void init() {
		log.info("StateMachineConfig .. init");
	}

	@Bean
	public StateMachineRuntimePersister<EmployeeState, EmpEvents, String> stateMachineRuntimePersister(
			JpaStateMachineRepository jpaStateMachineRepository) {
		return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}
	
	
	@Bean
	public StateMachinePersister<EmployeeState, EmpEvents, String> stateMachinePersister(StateMachinePersist<EmployeeState, EmpEvents, String> stateMachinePersist) {
		return new DefaultStateMachinePersister<>(stateMachinePersist);
	}

	@Bean
	public StateMachineService<EmployeeState, EmpEvents> stateMachineService(
			@Qualifier("employeeStateMachineFactory") StateMachineFactory<EmployeeState, EmpEvents> stateMachineFactory,
			StateMachineRuntimePersister<EmployeeState, EmpEvents, String> stateMachineRuntimePersister) {
  		return new DefaultStateMachineService<EmployeeState, EmpEvents>(stateMachineFactory, stateMachineRuntimePersister);
	}
	
	
	@Configuration
	@EnableStateMachineFactory(name= "employeeStateMachineFactory")
	@Slf4j
	public static class Config extends StateMachineConfigurerAdapter<EmployeeState, EmpEvents> {

				
		@PostConstruct
		public void init() {
			log.info("employeeStateMachineFactory .. init");
		}
			
		@Autowired
		private StateMachineRuntimePersister<EmployeeState, EmpEvents, String> stateMachineRuntimePersister;

		@Override
		public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmpEvents> config)
			throws Exception {
			config
				.withPersistence()
					.runtimePersister(stateMachineRuntimePersister)
					.and()
			        .withConfiguration()
			        .autoStartup(true);
		}

		@Override
		public void configure(StateMachineStateConfigurer<EmployeeState, EmpEvents> states) throws Exception {
			states
                .withStates()
                .initial(ADDED)
			                
                .fork(IN_CHECK)
                .join(ALL_CHECK_FINISHED)
                .state(APPROVED)
                .end(ACTIVE)
                .and()
                .withStates()
                .parent(IN_CHECK)
                .initial(SECURITY_CHECK_STARTED)
                .end(SECURITY_CHECK_FINISHED)
                .and()
                .withStates()
                .parent(IN_CHECK)
                .initial(WORK_PERMIT_CHECK_STARTED)
                .state(WORK_PERMIT_CHECK_PENDING_VERIFICATION)
                .end(EmployeeState.WORK_PERMIT_CHECK_FINISHED)
			                ;
			    }

			    @Override
			    public void configure(StateMachineTransitionConfigurer<EmployeeState, EmpEvents> transitions) throws Exception {
			        transitions
			        .withExternal()
                .source(ADDED).target(IN_CHECK).event(BEGIN_CHECK)
			                
                .and().withExternal()
                .source(WORK_PERMIT_CHECK_STARTED).target(WORK_PERMIT_CHECK_PENDING_VERIFICATION).event(COMPLETE_INITIAL_WORK_PERMIT_CHECK)
                .and().withExternal()
                .source(WORK_PERMIT_CHECK_PENDING_VERIFICATION).target(WORK_PERMIT_CHECK_FINISHED).event(FINISH_WORK_PERMIT_CHECK)
                .and().withExternal()
                .source(SECURITY_CHECK_STARTED).target(SECURITY_CHECK_FINISHED).event(FINISH_SECURITY_CHECK)
                .and()
                .withFork()
                .source(IN_CHECK)
                .target(WORK_PERMIT_CHECK_STARTED)
                .target(SECURITY_CHECK_STARTED)
			                    
			                    
                .and()
                .withJoin()
                .source(SECURITY_CHECK_FINISHED)
                .source(WORK_PERMIT_CHECK_FINISHED)
                .target(ALL_CHECK_FINISHED)
                .and()
                .withExternal()
			    .source(ALL_CHECK_FINISHED)
			                	.target(APPROVED)
			                	//.event("test")
                .and()
			    .withExternal()
			    .source(APPROVED)
			    .target(ACTIVE)
			    .event(ACTIVATE);
		}

		/*
		 * @Override public void configure(StateMachineModelConfigurer<EmployeeState,
		 * EmpEvents> model) throws Exception {
		 * 
		 * model.withModel().factory(modelFactory());
		 * 
		 * }
		 * 
		 * @Autowired private StateRepository<? extends RepositoryState>
		 * stateRepository;
		 * 
		 * @Autowired private TransitionRepository<? extends RepositoryTransition>
		 * transitionRepository;
		 * 
		 * 
		 * @Bean public StateMachineModelFactory modelFactory() {
		 * 
		 * StateMachineModelFactory fact = new RepositoryStateMachineModelFactory(
		 * stateRepository, transitionRepository); return fact; }
		 */
				
	}

	

}
