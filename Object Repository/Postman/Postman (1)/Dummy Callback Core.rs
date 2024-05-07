<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Dummy Callback Core</name>
   <tag></tag>
   <elementGuidId>11f30283-6877-41e8-a9d9-6a321b809992</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>true</autoUpdateContent>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\r\n  \&quot;callbackType\&quot;: \&quot;SIGNING_COMPLETE\&quot;,\r\n  \&quot;timeStamp\&quot;: \&quot;2023-08-20 16:43:51\&quot;,\r\n  \&quot;data\&quot;: {\r\n    \&quot;email\&quot;: \&quot;andy@ad-ins.com\&quot;,\r\n    \&quot;documentId\&quot;: \&quot;00155D0B-7502-A892-11EE-3A6D7CECE410\&quot;\r\n  },\r\n  \&quot;message\&quot;: \&quot;Success\&quot;\r\n}&quot;,
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
      <webElementGuid>62b1ca71-695e-495c-980a-99d373aa530e</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>x-api-key</name>
      <type>Main</type>
      <value>${api_key}</value>
      <webElementGuid>cdce197a-6b7d-4b46-8fcd-b1aea6d27891</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>9.0.0</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://dummy-ccallback-esignhub-dev-syimsualnf.ap-southeast-5.fcapp.run</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>-1</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.api_key</defaultValue>
      <description></description>
      <id>195e728b-edfa-401d-935e-fb35f7d6593a</id>
      <masked>false</masked>
      <name>api_key</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
