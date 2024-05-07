<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Check Saldo Sign</name>
   <tag></tag>
   <elementGuidId>dd8496b5-4caa-4f2d-bb02-a4cc0a9819fb</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;msg\&quot;: \&quot;jJwIXY4SY5WmgDDCFMaeezsXZMLFVqawcPY2Iu6jc4mostMBQ2Ob7ZahO5sGnTKC3l7mFz1R1MoVOfOG8N+Z+g\u003d\u003d\&quot;,\r\n    \&quot;tenantCode\&quot;: \&quot;TAFS\&quot;,\r\n    \&quot;vendorCode\&quot; : \&quot;VIDA\&quot;,\r\n    \&quot;listDocumentId\&quot;: [\r\n        \&quot;u1YI8EAIdLCHTx/Jtzb1EhF5Q+RmuingC92dPKbzz/fGkgh9EjH1uHANMam4QkkS\&quot;,\r\n        \&quot;HcsorMLJzu8v/sbZ5aePnrhoqlr2erq4J6D67sPBEmubusMN6y1bnvDPtddJvsx/\&quot;,\r\n        \&quot;nIhrhDsvLNY/Onbe/Jsj/WjSt0K3SrR+Ex5xnFeCN84CWAUc7MRz2S6IA0bquLAp\&quot;\r\n    ],\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;VIDA.ACCB@ESIGNHUB.MY.ID\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>a5f8d386-cb6f-4ff7-9187-8bdf4f3507ea</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>65fa4e62-ae4e-48e8-b1fd-3acadb9c37d5</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/embed/saldo/SignBalanceAvailabilityEmbed</restUrl>
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
      <id>bfdd5b29-2361-48f9-8ba3-4393f0e6c295</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
