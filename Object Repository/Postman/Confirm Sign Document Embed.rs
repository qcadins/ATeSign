<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Confirm Sign Document Embed</name>
   <tag></tag>
   <elementGuidId>5fbcd604-f05f-4780-a308-78e28ceacb3a</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <connectionTimeout>-1</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n  \&quot;audit\&quot;: {\n    \&quot;callerId\&quot;: \&quot;USER@AD-INS.COM\&quot;\n  },\n  \&quot;msg\&quot;: \&quot;Pvkay%2BO8L4Wg%2F1CNGMLhW21BjlToc9IcZxoHcrGf%2FmhV5oIPoHLlnfz2Jaz2nXwb7EX9MP8I6y3%2BzvnBTP263w%3D%3D\&quot;,\n  \&quot;tenantCode\&quot;: \&quot;WOMF\&quot;,\n  \&quot;ipAddress\&quot;: \&quot;172.89.10.10\&quot;,\n  \&quot;browser\&quot;: \&quot;mozila\&quot;,\n  \&quot;documentIds\&quot;: [\n      \&quot;hjFr3mS%2F9azmW4PKicNC%2FUXuiGpqdTtlRRUumHVr6c9t3Wpyzy09aABVEBuotRYJ\&quot;\n  ]\n}&quot;,
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
      <webElementGuid>005123e1-5186-4b8d-83ae-e1082f60b348</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>8.5.5</katalonVersion>
   <maxResponseSize>-1</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>${base_url}//services/embed/document/signConfirmDocument</restUrl>
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
      <id>3f34641f-f38e-4f54-a043-8a8320ba39da</id>
      <masked>false</masked>
      <name>base_url</name>
   </variables>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
