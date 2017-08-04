<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:param name="output_file"/>

    <xsl:template match="/">
        <ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1">
            <head></head>
            <docTitle>
                <text><xsl:value-of select="//rss/channel/title"/></text>
            </docTitle>
            <navMap>
                <navPoint id="toc" playOrder="1">
                    <navLabel><text>Table of Contents</text></navLabel>
                    <content src="{$output_file}_toc.htm#toc" />
                </navPoint>

                <xsl:for-each select="//rss/channel/item">
                    <navPoint id="{link}" playOrder="{position()+1}">
                        <navLabel><text><xsl:value-of select="title"/></text></navLabel>
                        <content src="{$output_file}.htm#{link}" />
                    </navPoint>
                </xsl:for-each>

            </navMap>
        </ncx>
    </xsl:template>

</xsl:stylesheet>