<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Get Template Document</name>
   <tag></tag>
   <elementGuidId>7660cc2c-51ad-4d4a-9fdc-c7d624b35c26</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n  \&quot;audit\&quot;: {\r\n    \&quot;callerId\&quot;: \&quot;ADMLEGAL@WOM.CO.ID\&quot;\r\n  },\r\n  \&quot;page\&quot;: 0,\r\n  \&quot;documentTemplateCode\&quot;: \&quot;DOC-SKPJFE\&quot;,\r\n  \&quot;isActive\&quot;: \&quot;1\&quot;,\r\n  \r\n  \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;\r\n}&quot;,
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
      <webElementGuid>e2ec7e3d-946e-4ed0-a7ad-b64074fcfa09</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>0cc22efb-f4b1-474f-b475-fc9786d73cc4</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${token}</value>
      <webElementGuid>da89c0f7-485f-4a96-a460-b7e2ec6090e5</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/document/s/getDocumentTemplate</restUrl>
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
      <id>e43686ac-1aca-4418-9f72-4eef3f9687e6</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.token</defaultValue>
      <description></description>
      <id>1661f6ea-de70-4458-b3bf-973b2fb12877</id>
      <masked>false</masked>
      <name>token</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>