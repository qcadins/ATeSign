<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Register BM</name>
   <tag></tag>
   <elementGuidId>6f626868-6e13-49f9-847e-0cbdc6a6a8e1</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;userData\&quot;:       {\r\n            \&quot;idKtp\&quot;: \&quot;250000110850008\&quot;,\r\n            \&quot;nama\&quot;: \&quot;BM Dummy2\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;M\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;SUBANG\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;1985-10-01\&quot;,\r\n            \&quot;email\&quot;: \&quot;BM.SPRINT25@ESIGNHUB.MY.ID\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;082525000081\&quot;,\r\n            \&quot;kUser\&quot;: \&quot;YaMYIedgY1s1mabu\&quot;,\r\n            \&quot;cvv\&quot; : \&quot;222\&quot;,\r\n            \&quot;poaId\&quot; : \&quot;asdasdaaa\&quot;\r\n        },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;vendorCode\&quot;: \&quot;VIDA\&quot;,\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;ADINS\&quot;\r\n    }\r\n}\r\n&quot;,
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
      <webElementGuid>beff337c-ac25-44cb-8838-7fa1741aef52</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>d7f002d4-6145-4dc1-ae1f-e9123c5f6ea9</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${sim_url}/services/user/registerBM</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.sim_url</defaultValue>
      <description></description>
      <id>9926dd2e-d063-4030-897c-0b139a0cb76f</id>
      <masked>false</masked>
      <name>sim_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
