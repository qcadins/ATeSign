<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Send 2 Doc VIDA UAT Autosign Copy</name>
   <tag></tag>
   <elementGuidId>5122dc5b-de1d-4a44-9d87-e7f01d2f3c4f</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>85a0e0e9-daf2-4070-9da6-eb05208bbc90</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>9364fd70-6600-434b-9b3f-5342556a4d4c</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>${api_key}@${tenant_code}</value>
      <webElementGuid>a7497d3a-e86c-46dc-af0e-2001ef9880b4</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${uat_url}/services/confins/document/send</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.uat_url</defaultValue>
      <description></description>
      <id>3f55d5be-f05a-4420-8a8a-2646b99d4863</id>
      <masked>false</masked>
      <name>uat_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.api_key</defaultValue>
      <description></description>
      <id>8371de19-6a9b-452a-b471-a57466529a37</id>
      <masked>false</masked>
      <name>api_key</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.tenant_code</defaultValue>
      <description></description>
      <id>0289e530-40f0-41ab-8e00-3eb9f6eac644</id>
      <masked>false</masked>
      <name>tenant_code</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>