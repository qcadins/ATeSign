<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Check Registration Status</name>
   <tag></tag>
   <elementGuidId>2529831a-9575-41bb-b94e-07aeb8114aaf</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n\&quot;by_field\&quot;: \&quot;request_id\&quot;,\n\&quot;value\&quot;: \&quot;FULL-ADINS-ESIGNHUB-2023-ANDY-005\&quot;\n}&quot;,
  &quot;contentType&quot;: &quot;text/plain&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Basic e3t1c2VybmFtZX19Ont7cGFzc3dvcmR9fQ==</value>
      <webElementGuid>d98c5f19-d1cf-4dd8-b280-272668bde5c0</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Merchant-Key</name>
      <type>Main</type>
      <value>${merchant-key}</value>
      <webElementGuid>8bd9d1e9-91f8-458f-bc45-19c700bbc5f3</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://stg-b2b-api.privy.id/ex/v1/registration/status</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.merchant-key</defaultValue>
      <description></description>
      <id>4b81d8c0-9f0a-4f83-9da9-af27e597e8f0</id>
      <masked>false</masked>
      <name>merchant-key</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
