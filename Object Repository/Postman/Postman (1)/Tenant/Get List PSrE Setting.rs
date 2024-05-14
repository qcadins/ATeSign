<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Get List PSrE Setting</name>
   <tag></tag>
   <elementGuidId>b1f0b49f-31ff-4e8d-aabe-7624be94f193</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;default\&quot;\r\n    },\r\n    \&quot;vendorName\&quot;: \&quot;\&quot;,\r\n    \&quot;vendorCode\&quot;: \&quot;\&quot;,\r\n    \&quot;isActive\&quot;: \&quot;\&quot;,\r\n    \&quot;isOperating\&quot;: \&quot;\&quot;,\r\n    \&quot;page\&quot;: \&quot;1\&quot;\r\n}&quot;,
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
      <webElementGuid>f3baa628-ffc2-4b67-a799-20f0fa20cd3e</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>f99a889c-47b7-45bd-821b-7f49bfa846b4</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>c8324683-d466-496a-a5ee-19cafe5add5d</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/data/s/getListPSrESetting</restUrl>
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
      <id>eb8e61dd-27cd-497a-9fb6-a459e9df9df6</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>68b721cc-a2ad-4b0a-b220-4b8d7c4a0db5</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>