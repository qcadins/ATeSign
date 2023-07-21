<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>API Upload Document Copy</name>
   <tag></tag>
   <elementGuidId>dcb86094-5725-4c74-974c-f97381a3b203</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;parameters&quot;: [
    {
      &quot;name&quot;: &quot;documentTitle&quot;,
      &quot;value&quot;: &quot;ANDY-SIGNER-TEST010&quot;,
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
      &quot;value&quot;: &quot;[ { \&quot;privyId\&quot;: \&quot;MNI5025\&quot;, \&quot;type\&quot;: \&quot;Signer\&quot;, \&quot;enterpriseToken\&quot;: \&quot;\&quot;, \&quot;signature\&quot;: \&quot;2\&quot;, \&quot;signMultiple\&quot;: true, \&quot;signCoordinates\&quot;: [ { \&quot;signPage\&quot;: 4, \&quot;posX\&quot;: 61, \&quot;posY\&quot;: 126}, { \&quot;signPage\&quot;: 11, \&quot;posX\&quot;: 45, \&quot;posY\&quot;: 264} ] }, { \&quot;privyId\&quot;: \&quot;ADD2843\&quot;, \&quot;type\&quot;: \&quot;Signer\&quot;, \&quot;enterpriseToken\&quot;: \&quot;\&quot;, \&quot;signature\&quot;: \&quot;3\&quot;,\&quot;draggable\&quot;: \&quot;true\&quot; , \&quot;signMultiple\&quot;: true, \&quot;signCoordinates\&quot;: [ { \&quot;signPage\&quot;: 4, \&quot;posX\&quot;: 70, \&quot;posY\&quot;: 278}, { \&quot;signPage\&quot;: 11, \&quot;posX\&quot;: 38, \&quot;posY\&quot;: 272}, { \&quot;signPage\&quot;: 5, \&quot;posX\&quot;: 256, \&quot;posY\&quot;: 56} ] } ]&quot;,
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
      <webElementGuid>1aa71ab2-53d8-4002-a7ec-daea0b4c089c</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>merchant-key</name>
      <type>Main</type>
      <value>${merchant-key}</value>
      <webElementGuid>18dadded-4a01-4e49-ae34-2331d8eecbd3</webElementGuid>
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
      <id>58250156-e261-4792-8623-da408c804db9</id>
      <masked>false</masked>
      <name>merchant-key</name>
   </variables>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
