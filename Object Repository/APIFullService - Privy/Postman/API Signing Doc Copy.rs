<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>API Signing Doc Copy</name>
   <tag></tag>
   <elementGuidId>904e268e-8d7e-40a8-b92c-98b650874b5c</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n \&quot;documents\&quot;: [\n  {\n   \&quot;docToken\&quot;: \&quot;f533d44d701a908997d5b4f7fff0cf08bb1cf110d2c4db420dd695fb14a1ce39\&quot;,\n        \&quot;x\&quot;:\&quot;64\&quot;,\n        \&quot;y\&quot;:\&quot;127\&quot;,\n        \&quot;page\&quot;:\&quot;4\&quot;\n  }\n  ],\n \&quot;signature\&quot;: {\n  \&quot;visibility\&quot; : true\n }\n}&quot;,
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
      <webElementGuid>ff9bd540-a086-4a41-9ada-b2c89cc91f38</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Merchant-Key</name>
      <type>Main</type>
      <value>${merchant-key}</value>
      <webElementGuid>b1537b34-b539-4f9d-8834-92a28611c4cb</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Token</name>
      <type>Main</type>
      <value>${access_token}</value>
      <webElementGuid>51e79841-07a3-4927-8dac-b9511aedb4e0</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://stg-core.privy.id/v2/merchant/document/multiple-signing</restUrl>
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
      <id>88b9f404-539c-4d8c-ac34-da71a74470bb</id>
      <masked>false</masked>
      <name>merchant-key</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.access_token</defaultValue>
      <description></description>
      <id>5725e414-b382-40a3-82ab-c82b79ad35fd</id>
      <masked>false</masked>
      <name>access_token</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>