<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:content="http://purl.org/rss/1.0/modules/content/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:epub="http://www.idpf.org/2007/ops">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:param name="output_file"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="//rss/channel/title"/>
                </title>
            </head>
            <body>
                <div id="toc">
                    <h2>Table of Contents</h2>
                    <ol>
                        <xsl:for-each select="//rss/channel/item">
                            <li><a href="{$output_file}.htm#{link}"><xsl:value-of select="title"/></a></li>
                        </xsl:for-each>
                    </ol>
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>