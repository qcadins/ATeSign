<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Inquiry Document Embed</name>
   <tag></tag>
   <elementGuidId>cfc02824-814a-4571-9f66-b3ceb5a8ac80</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;msg\&quot;: \&quot;jJwIXY4SY5WmgDDCFMaeezsXZMLFVqawcPY2Iu6jc4mostMBQ2Ob7ZahO5sGnTKC3l7mFz1R1MoVOfOG8N+Z+g\u003d\u003d\&quot;,\r\n    \&quot;tenantCode\&quot;: \&quot;TAFS\&quot;,\r\n    \&quot;transactionStatus\&quot;: \&quot;NS\&quot;,\r\n    \&quot;page\&quot;: 1,\r\n    \&quot;inquiryType\&quot;: \&quot;inbox\&quot;,\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;CONFINS\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>6b4731f7-4934-4727-8cf5-e8b5d8157819</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>d95c42a7-6569-4d6d-ba4d-3fe33dbe08ef</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/embed/document/inquiryEmbed</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.base_url</defaultValue>
      <description></description>
      <id>d560af3b-0cf0-4ad0-a280-f244f203dac7</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
