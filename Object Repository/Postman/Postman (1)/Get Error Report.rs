<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Get Error Report</name>
   <tag></tag>
   <elementGuidId>3af20fab-29e0-47b1-ac3d-e3cb4db6db4a</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;tenantCode\&quot;:\&quot;WOMF\&quot;,\r\n    \&quot;page\&quot;: 1,\r\n    \&quot;modul\&quot;: \&quot;\&quot;,\r\n    \&quot;refNumber\&quot;:\&quot;\&quot;,\r\n    \&quot;namaKonsumen\&quot;:\&quot;\&quot;,\r\n    \&quot;cabang\&quot;:\&quot;\&quot;,\r\n    \&quot;region\&quot;:\&quot;\&quot;,\r\n    \&quot;businessLine\&quot;:\&quot;\&quot;,\r\n    \&quot;tanggalDari\&quot;:\&quot;2022-04-20\&quot;,\r\n    \&quot;tanggalSampai\&quot;:\&quot;2022-04-20\&quot;,\r\n    \&quot;tipe\&quot;:\&quot;\&quot;,\r\n     \&quot;audit\&quot;: {\r\n\r\n        \&quot;callerId\&quot;: \&quot;TEST\&quot;\r\n    }\r\n}&quot;,
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
      <webElementGuid>9caf6d43-e3e9-4428-9e4f-93dd1d458815</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>7865dab3-1e88-42ee-9f07-0422044ff682</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer AeEhLyteZwSDYeS8KKDgi+beUhE=</value>
      <webElementGuid>0d2f06df-7fe3-4f89-af4c-dfc1c14e2f88</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}/services/errorReport/s/errorHistory</restUrl>
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
      <id>5804ad7e-61f9-4fb5-9196-239a19cd8c54</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
