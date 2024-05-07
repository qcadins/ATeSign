<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Request Link (sim) Copy</name>
   <tag></tag>
   <elementGuidId>16d07efb-e4ca-43e5-ad04-6a5a89953352</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n    \&quot;audit\&quot;: {\r\n        \&quot;callerId\&quot;: \&quot;mobile\&quot;\r\n    },\r\n    \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\r\n    \&quot;users\&quot;: [\r\n        {\r\n            \&quot;nama\&quot;: \&quot;UserBDGA\&quot;,\r\n            \&quot;tlp\&quot;: \&quot;081000991360\&quot;,\r\n            \&quot;email\&quot;: \&quot;\&quot;,\r\n            \&quot;jenisKelamin\&quot;: \&quot;F\&quot;,\r\n            \&quot;tmpLahir\&quot;: \&quot;BOGOR\&quot;,\r\n            \&quot;tglLahir\&quot;: \&quot;1980-01-01\&quot;,\r\n            \&quot;idKtp\&quot;: \&quot;3511000101801360\&quot;,\r\n            \&quot;provinsi\&quot;: \&quot;Jawa Barat\&quot;,\r\n            \&quot;kota\&quot;: \&quot;Bogor\&quot;,\r\n            \&quot;kecamatan\&quot;: \&quot;Bogor Selatan\&quot;,\r\n            \&quot;kelurahan\&quot;: \&quot;Baranangsiang\&quot;,\r\n            \&quot;kodePos\&quot;: \&quot;16143\&quot;,\r\n            \&quot;alamat\&quot;: \&quot;JL. SAWOKNA NO.1000 BANTAR KEMANG\&quot;\r\n        }\r\n    ]\r\n}&quot;,
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
      <webElementGuid>4d291ca5-96c1-4366-940b-bffdb18f26f6</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>ASDFGH@WOMF</value>
      <webElementGuid>64bba9b4-cd97-4305-b9ab-895d063e6758</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${sim_url}/services/user/generateInvitationLink</restUrl>
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
      <id>1f133de4-f93c-4525-b56c-a1e1bf78ed09</id>
      <masked>false</masked>
      <name>sim_url</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
