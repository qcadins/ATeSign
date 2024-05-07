<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Add Tenant</name>
   <tag></tag>
   <elementGuidId>e08338ba-132f-42bc-ac27-1a36623e3c0a</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;ADMESIGN\&quot;\r\n    },\r\n    \&quot;emailReminderDest\&quot;: [\&quot;\&quot;],\r\n    \&quot;tenantCode\&quot;: \&quot;TESKSG1\&quot;,\r\n    \&quot;tenantName\&quot;: \&quot;TES KOSONG 1\&quot;,\r\n    \&quot;apiKey\&quot;: \&quot;123\&quot;,\r\n    \&quot;thresholdBalance\&quot;: {\r\n        \&quot;OTP\&quot;: 1,\r\n        \&quot;VRF\&quot;: 1,\r\n        \&quot;DOC\&quot;: 1,\r\n        \&quot;SDT\&quot;: 1,\r\n        \&quot;SMS\&quot;: 1,\r\n        \&quot;FVRF\&quot;: 1,\r\n        \&quot;SGN\&quot;: 1\r\n    },\r\n    \&quot;refNumberLabel\&quot;: \&quot;Nomor Kontrak\&quot;,\r\n    \&quot;emailUserAdmin\&quot;: \&quot;ADMIN@TENANTTEST3\&quot;,\r\n    \&quot;passwordUserAdmin\&quot;: \&quot;123\&quot;\r\n}\r\n&quot;,
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
      <webElementGuid>47f6ce80-179a-42cc-8ef6-525f3a69c93a</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>5479d76a-1d70-4fd0-9354-03b472cf523e</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer 5fe3a6c5-1923-4977-9b73-b8b96c022fc7</value>
      <webElementGuid>71faca3d-9651-4cea-89ba-d7a2ad2ed1a6</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/tenant/s/addTenant</restUrl>
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
      <id>9b7738ad-a2db-4b18-b582-6b62accec990</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
