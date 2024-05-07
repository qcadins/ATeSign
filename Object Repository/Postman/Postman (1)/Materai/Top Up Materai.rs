<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Top Up Materai</name>
   <tag></tag>
   <elementGuidId>5c2523f7-f571-46e5-b08e-712b89a76efa</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n  \&quot;audit\&quot;: {\r\n    \&quot;callerId\&quot;: \&quot;ADMIN@WOM.CO.ID\&quot;\r\n  },\r\n  \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n  \&quot;vendorCode\&quot;: \&quot;DJP\&quot;,\r\n  \&quot;loginId\&quot;: \&quot;ADMIN@WOM.CO.ID\&quot;,\r\n  \&quot;stampDutyInvNo\&quot;: \&quot;INV/STAMP/01\&quot;,\r\n  \&quot;stampDutyFee\&quot;: \&quot;10000\&quot;,\r\n  \&quot;stampDutyDate\&quot;: \&quot;2021-10-01\&quot;,\r\n  \&quot;stampDutyQty\&quot;: \&quot;1\&quot;,\r\n  \&quot;notes\&quot;: \&quot;TES TOP UP 4\&quot;\r\n}&quot;,
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
      <webElementGuid>045896b4-137a-4d18-a95e-0f11822a7f94</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>5c4e8ee0-a673-44d4-9977-95139f85f648</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer 5e7f4fbe-2558-4248-9987-e1d86c8265ad</value>
      <webElementGuid>35842d17-d586-436a-befb-1cfcff137cc8</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/stampduty/s/topup</restUrl>
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
      <id>5b3bacb4-b01a-4b19-9d5f-21d02033f606</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
