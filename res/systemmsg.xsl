<?xml version="1.0" encoding="iso-8859-2" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<html><body>
		<xsl:apply-templates />
	</body></html>
</xsl:template>
<xsl:template match="systemmsg">
	System message from <xsl:value-of select="@sendertype" /> id <xsl:value-of select="@senderid" />: "<xsl:value-of select="." />"<br />
</xsl:template>
</xsl:stylesheet>