<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE xsl [
  <!ENTITY crm "http://erlangen-crm.org/current/">
  <!ENTITY foaf "http://xmlns.com/foaf/0.1/">
  <!ENTITY lido "http://www.lido-schema.org/">
  <!ENTITY skos "http://www.w3.org/2004/02/skos/core#">
  <!ENTITY bibo "http://purl.org/ontology/bibo/">
  <!ENTITY dcterms "http://purl.org/dc/terms/">
  <!ENTITY geo-pos "http://www.w3.org/2003/01/geo/wgs84_pos#">
  
  <!ENTITY bmo "http://collection.britishmuseum.org/id/ontology/">
  
  <!ENTITY getty_tgn "http://vocab.getty.edu/resource/tgn/subject/">	
  <!ENTITY getty_aat "http://vocab.getty.edu/resource/aat/subject/">
  <!ENTITY getty_ulan "http://vocab.getty.edu/resource/ulan/subject/">

  <!ENTITY viaf "http://viaf.org/viaf/">
  <!ENTITY odnb "http://www.oxforddnb.com/view/article/">
  <!--ENTITY dbpedia "http://dbpedia.org/resource/"-->
  <!--ENTITY freebase "http://rdf.freebase.com/ns/"-->
  <!ENTITY iconclass "http://www.iconclass.org/rkd/">
  
  <!ENTITY loc_subjects "http://id.loc.gov/authorities/subjects/">
  <!ENTITY loc_names "http://id.loc.gov/authorities/names/">
	
  <!ENTITY ycba "http://collection.britishart.yale.edu/id/">
  <!ENTITY ycba_dimension "http://collection.britishart.yale.edu/id/thesauri/dimension/">
  
  <!ENTITY oclc "http://www.worldcat.org/oclc/">
  <!ENTITY bmo "http://collection.britishmuseum.org/id/ontology/">
  <!ENTITY ycba_img_base "http://deliver.odai.yale.edu/info/repository/YCBA/id/">
  <!ENTITY ycba_base "http://collection.britishart.yale.edu/">
  <!ENTITY ycba_thesauri "http://collection.britishart.yale.edu/id/thesauri/">
  <!ENTITY ycba_img_flag "http://collection.britishart.yale.edu/id/thesauri/image/flag/">
  <!ENTITY ycba_img_format "http://collection.britishart.yale.edu/id/thesauri/image/format/">
  <!ENTITY yale_iiif "http://scale.ydc2.yale.edu/iiif/">
  <!ENTITY xsd "http://www.w3.org/2001/XMLSchema#">
]>

<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
>
    <xsl:output method="html"
            encoding="UTF-8"
            indent="yes"
			omit-xml-declaration="yes"/>	
    <xsl:template match="//contentSet">
		<xsl:param name="MAIN_IMG_FORMAT_ID">2</xsl:param>
		<xsl:param name="DEEP_ZOOM_FORMAT_ID">7</xsl:param>
		<rdf:RDF
			xmlns:bmo="http://collection.britishmuseum.org/id/ontology/"
			xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
			xmlns:skos="http://www.w3.org/2004/02/skos/core#"
			xmlns:crm="http://erlangen-crm.org/current/"
			xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:dct="http://purl.org/dc/terms/"
			xmlns:exif="http://www.w3.org/2003/12/exif/ns#"
			xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
			
		>
			<xsl:for-each select="content">
			
			<!-- Objects/Images -->
				<xsl:for-each select="derivative">
					<xsl:variable name="fileName" select="tokenize(@filename, '-')" />
					<xsl:element name="rdf:Description">
						<xsl:attribute name="rdf:about" select="concat('&ycba;object/', $fileName[3])"/>
						<xsl:choose>
							<xsl:when test="@formatId = $MAIN_IMG_FORMAT_ID">
								<xsl:element name="bmo:PX_has_main_representation">
									<xsl:attribute name="rdf:resource" select="@url"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="@formatId = $DEEP_ZOOM_FORMAT_ID">
								<xsl:element name="crm:P138i_has_representation ">
									<xsl:attribute name="rdf:resource" select="concat('&yale_iiif;', ../@contentId)"/>
								</xsl:element>
							</xsl:when>
							<xsl:otherwise>
								<xsl:element name="crm:P138i_has_representation ">
									<xsl:attribute name="rdf:resource" select="@url"/>
								</xsl:element>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:element>
				</xsl:for-each>
			</xsl:for-each>
		</rdf:RDF>
    </xsl:template>
</xsl:stylesheet>