/*
 * Copyright 2019 the original author or authors.
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

import static com.workmotion.statemachine.EmployeeState.ACTIVE;
import static com.workmotion.statemachine.EmployeeState.APPROVED;
import static com.workmotion.statemachine.EmployeeState.IN_CHECK;
import static com.workmotion.statemachine.EmployeeState.SECURITY_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.SECURITY_CHECK_STARTED;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_FINISHED;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_PENDING_VERIFICATION;
import static com.workmotion.statemachine.EmployeeState.WORK_PERMIT_CHECK_STARTED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.workmotion.Application;
import com.workmotion.controller.dto.EmployeeDTO;
import com.workmotion.controller.dto.EmployeeEventDTO;



@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { Application.class})
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class EmployeeStateMachineIntegrationTest {

	private MockMvc mvc;
	
	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext context;

	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Test
	public void testcreate() throws Exception { 
	    MvcResult result = createEmployee();
	    
	    
	    JSONObject jsonObject= new JSONObject(result.getResponse().getContentAsString());
	    JSONArray states= new JSONArray("[\"ADDED\"]");
	    Assertions.assertTrue(states.equals(jsonObject.get("states")));
	    
	}
	
	@Test
	public void testUpdateState1() throws Exception {
		MvcResult result = createEmployee();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		String id = jsonObject.get("id").toString();
		
		testEmployeeState(id, mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.BEGIN_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.FINISH_SECURITY_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_PENDING_VERIFICATION)), EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK);
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(APPROVED)), EmpEvents.FINISH_WORK_PERMIT_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(ACTIVE)), EmpEvents.ACTIVATE);
	    
	}
	
	
	
	@Test
	public void testUpdateState2() throws Exception {
		MvcResult result = createEmployee();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		String id = jsonObject.get("id").toString();
		
		testEmployeeState(id, mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.BEGIN_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_PENDING_VERIFICATION)), EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED)), EmpEvents.FINISH_WORK_PERMIT_CHECK);
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(APPROVED)), EmpEvents.FINISH_SECURITY_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(ACTIVE)), EmpEvents.ACTIVATE);
		
	}
	
	@Test
	public void testUpdateState3() throws Exception {
		MvcResult result = createEmployee();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		String id = jsonObject.get("id").toString();
		
		testEmployeeState(id, mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.BEGIN_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_PENDING_VERIFICATION)), EmpEvents.COMPLETE_INITIAL_WORK_PERMIT_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_PENDING_VERIFICATION)), EmpEvents.FINISH_SECURITY_CHECK);
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(APPROVED)), EmpEvents.FINISH_WORK_PERMIT_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(ACTIVE)), EmpEvents.ACTIVATE);
		
		
	}
	
	@Test
	public void testUpdateState4() throws Exception {
		MvcResult result = createEmployee();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		String id = jsonObject.get("id").toString();
		
		testEmployeeState(id, mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.BEGIN_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.FINISH_SECURITY_CHECK);
	    
		testUpdateEmployeeStateResponseCode(id, EmpEvents.ACTIVATE, status().is4xxClientError());
	}
	
	@Test
	public void testUpdateState5() throws Exception {
		MvcResult result = createEmployee();

		JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
		String id = jsonObject.get("id").toString();
		
		testEmployeeState(id, mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.BEGIN_CHECK);
	    
		testEmployeeState(id, 
				mapper.writeValueAsString(Set.of(IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED)), EmpEvents.FINISH_SECURITY_CHECK);
	    
		testUpdateEmployeeStateResponseCode(id, EmpEvents.FINISH_SECURITY_CHECK, status().is4xxClientError());
	}
	
	
	private MvcResult testUpdateEmployeeStateResponseCode(String id, EmpEvents event, ResultMatcher resultMatcher) throws JsonProcessingException, Exception {
		String url =  "/employee/"+id+"/state";
		EmployeeEventDTO dto = new EmployeeEventDTO();
	    dto.setEmpEvent(event);
	    
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto );

	    MvcResult result = mvc.perform(put(url).contentType(APPLICATION_JSON_UTF8)
	        .content(requestJson))
	        .andExpect(resultMatcher).andDo(print()).andReturn();
		return result;
	}
	
	
	
	
	

	private void testEmployeeState(String id, String expectedState, EmpEvents testEvent)
			throws JsonProcessingException, Exception, JSONException, UnsupportedEncodingException {
		MvcResult updateResult = updateEmployeeState(id, testEvent);
		JSONObject jsonOb= new JSONObject(updateResult.getResponse().getContentAsString());
		JSONArray states= new JSONArray(expectedState);
	   //assertTrue(states.equals(states));
	   JSONAssert.assertEquals(states, jsonOb.getJSONArray("states"), JSONCompareMode.NON_EXTENSIBLE);
	}


	private MvcResult updateEmployeeState(String id, EmpEvents event) throws JsonProcessingException, Exception {
		String url =  "/employee/"+id+"/state";
		EmployeeEventDTO dto = new EmployeeEventDTO();
	    dto.setEmpEvent(event);
	    
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto );

	    MvcResult result = mvc.perform(put(url).contentType(APPLICATION_JSON_UTF8)
	        .content(requestJson))
	        .andExpect(status().isOk()).andDo(print()).andReturn();
		return result;
	}
	



	private MvcResult createEmployee() throws JsonProcessingException, Exception {
		String url =  "/employee";
	    EmployeeDTO dto = new EmployeeDTO();
	    dto.setName("name");
	    
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
	    String requestJson=ow.writeValueAsString(dto );

	    MvcResult result = mvc.perform(post(url).contentType(APPLICATION_JSON_UTF8)
	        .content(requestJson))
	        .andExpect(status().isOk()).andDo(print()).andReturn();
		return result;
	}
	
	

	



	@BeforeEach
	public void setup() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
}
