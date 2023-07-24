<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>API Upload Document Copy 2</name>
   <tag></tag>
   <elementGuidId>ba22a8ee-ef5a-4cd9-b4fa-6a362e4450b5</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;parameters&quot;: [
    {
      &quot;name&quot;: &quot;documentTitle&quot;,
      &quot;value&quot;: &quot;POC-SIGNER-TEST004&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;docType&quot;,
      &quot;value&quot;: &quot;Parallel&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;owner&quot;,
      &quot;value&quot;: &quot;{\&quot;privyId\&quot;:\&quot;ADD2843\&quot;, \&quot;enterpriseToken\&quot;: \&quot;{{enterprise_token}}\&quot; }&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;recipients&quot;,
      &quot;value&quot;: &quot;[ { \&quot;privyId\&quot;: \&quot;MNI5025\&quot;, \&quot;type\&quot;: \&quot;Signer\&quot;, \&quot;enterpriseToken\&quot;: \&quot;\&quot;}, { \&quot;privyId\&quot;: \&quot;ADD2843\&quot;, \&quot;type\&quot;: \&quot;Signer\&quot;, \&quot;enterpriseToken\&quot;: \&quot;\&quot;} ]&quot;,
      &quot;type&quot;: &quot;text&quot;,
      &quot;contentType&quot;: &quot;&quot;
    },
    {
      &quot;name&quot;: &quot;document&quot;,
      &quot;type&quot;: &quot;file&quot;,
      &quot;contentType&quot;: &quot;&quot;
    }
  ]
}</httpBodyContent>
   <httpBodyType>form-data</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Basic e3t1c2VybmFtZX19Ont7cGFzc3dvcmR9fQ==</value>
      <webElementGuid>edf61212-fb82-45c2-be60-e511fbf1ede9</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>merchant-key</name>
      <type>Main</type>
      <value>${merchant-key}</value>
      <webElementGuid>0e4d20ee-e5d9-4976-8aab-bb2fe9eb4abf</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>https://stg-core.privy.id/v3/merchant/document/upload</restUrl>
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
      <id>a9b18ffe-46a2-41da-b065-085186a27473</id>
      <masked>false</masked>
      <name>merchant-key</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
