<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update Tenant Setting</name>
   <tag></tag>
   <elementGuidId>e796e7b3-cb06-4407-9aa2-cce0dfbb7d2d</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;ADMIN@WOM.CO.ID\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;refNumberLabel\&quot;: \&quot;No Kontrak : \&quot;,\r\n    \&quot;thresholdBalance\&quot;: {\r\n        \&quot;SDT_POSTPAID\&quot;: 1000,\r\n        \&quot;LIVENESS_FACECOMPARE\&quot;: 0,\r\n        \&quot;SDT\&quot;: 1000,\r\n        \&quot;LIVENESS\&quot;: 0,\r\n        \&quot;FACECOMPARE\&quot;: 0,\r\n        \&quot;SMS\&quot;: 0,\r\n        \&quot;DOC\&quot;: 1000,\r\n        \&quot;PNBP\&quot;: 0,\r\n        \&quot;OTP\&quot;: 1000,\r\n        \&quot;VRF\&quot;: 1000,\r\n        \&quot;SGN\&quot;: 0\r\n    },\r\n    \&quot;emailReminderDest\&quot;: [\r\n        \&quot;ANDY@AD-INS.COM\&quot;\r\n    ],\r\n    \&quot;activationCallbackUrl\&quot;: \&quot;http://bb45920e-a479-47e7-a138-4bde27802b4e.mock.pstmn.io/activationCallbackSuccess\&quot;,\r\n    \&quot;automaticSign\&quot;: false,\r\n    \&quot;useWaMessage\&quot;: \&quot;0\&quot;,\r\n    \&quot;clientCallbackUrl\&quot;: \&quot;https://trycallback.requestcatcher.com/\&quot;,\r\n    \&quot;clientActivationRedirectUrl\&quot;: \&quot;https://trycallback.requestcatcher.com/\&quot;,\r\n    \&quot;clientSigningRedirectUrl\&quot;: \&quot;https://trycallback.requestcatcher.com/\&quot;\r\n}&quot;,
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
      <webElementGuid>1d3cefdf-4d9d-4fc5-8315-7fcc620e1b95</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>bearer ${token}</value>
      <webElementGuid>87d64141-c82f-4cb1-a0dd-bd20a9b59579</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/tenant/s/updateTenantSettings</restUrl>
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
      <id>15ab2310-2082-43cc-93b7-f42b072e3483</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>f2740adf-9250-431d-a22e-a9204a147152</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
