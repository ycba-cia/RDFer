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
  <!ENTITY ycba_img_flag "http://collection.britishart.yale.edu/id/thesauri/aspect">
  <!ENTITY ycba_img_format "http://collection.britishart.yale.edu/id/thesauri/format">
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
			<!-- Properties -->
			<xsl:element name="rdf:Description">
				<xsl:attribute name="rdf:about" select="'&bmo;PX_image_flag'"/>
				<xsl:element name="rdfs:subPropertyOf">
					<xsl:attribute name="rdf:resource" select="'&crm;P2_has_type'"/>
				</xsl:element>
				<xsl:element name="rdfs:label">Image Flag</xsl:element>
				<xsl:element name="rdfs:comment ">eg recto, verso, cropped, X-Ray</xsl:element>
			</xsl:element>
			<xsl:element name="rdf:Description">
				<xsl:attribute name="rdf:about" select="'&bmo;PX_image_format '"/>
				<xsl:element name="rdfs:subPropertyOf">
					<xsl:attribute name="rdf:resource" select="'&crm;P2_has_type'"/>
				</xsl:element>
				<xsl:element name="rdfs:label">Image Format</xsl:element>
				<xsl:element name="rdfs:comment ">eg Small, Medium, Large</xsl:element>
			</xsl:element>
			
			<xsl:for-each select="content">
			
			<!-- Thesauri -->
				<xsl:element name="rdf:Description">
					<xsl:attribute name="rdf:about" select="'&ycba_img_flag;'"/>
					<xsl:element name="rdf:type">
						<xsl:attribute name="rdf:resource" select="concat('&skos;', 'ConceptScheme')"/>
					</xsl:element>
					<xsl:element name="skos:prefLabel">Image Flag</xsl:element>
				</xsl:element>
				<xsl:element name="rdf:Description">
					<xsl:attribute name="rdf:about" select="'&ycba_img_format;'"/>
					<xsl:element name="rdf:type">
						<xsl:attribute name="rdf:resource" select="concat('&skos;', 'ConceptScheme')"/>
					</xsl:element>
					<xsl:element name="skos:prefLabel">Image Format</xsl:element>
				</xsl:element>
				
			<!-- Terms - Flags -->
				<xsl:variable name="imgFlags" select="tokenize(metadata/@caption, ', ')" />
				<xsl:for-each select="$imgFlags">
					<xsl:variable name="counter" select="position()"/>
					<xsl:variable name="imgFlagPieces" select="tokenize($imgFlags[$counter], '\s+')" />
					<xsl:element name="rdf:Description">
						<xsl:attribute name="rdf:about" select="concat('&ycba_img_flag;', string-join(($imgFlagPieces), '_'))"/>
						<xsl:element name="rdf:type">
							<xsl:attribute name="rdf:resource" select="concat('&crm;', 'E55_Type')"/>
						</xsl:element>
						<xsl:element name="rdf:type">
							<xsl:attribute name="rdf:resource" select="concat('&skos;', 'Concept')"/>
						</xsl:element>
						<xsl:element name="skos:prefLabel"><xsl:value-of select="$imgFlags[$counter]"/></xsl:element>
						<xsl:element name="skos:inScheme">
							<xsl:attribute name="rdf:resource" select="'&ycba_img_flag;'"/>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
				
			<!-- Terms - Format -->
				<xsl:for-each select="derivative">
					<xsl:element name="rdf:Description">
						<xsl:variable name="formatUri" select="string-join(('&ycba_img_format;', @formatShort), '')"/>
						<xsl:attribute name="rdf:about" select="$formatUri"/>
						<xsl:element name="rdf:type">
							<xsl:attribute name="rdf:resource" select="concat('&crm;', 'E55_Type')"/>
						</xsl:element>
						<xsl:element name="rdf:type">
							<xsl:attribute name="rdf:resource" select="concat('&skos;', 'Concept')"/>
						</xsl:element>
						<xsl:element name="skos:prefLabel"><xsl:value-of select="@label"/></xsl:element>
						<xsl:element name="skos:inScheme">
							<xsl:attribute name="rdf:resource" select="'&ycba_img_format;'"/>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
				
			<!-- Rights -->
				<xsl:variable name="usageUri" select="'&ycba_base;collections/using-collections/image-use'" />
				<xsl:element name="rdf:Description">
					<xsl:attribute name="rdf:about" select="$usageUri"/>
					<xsl:element name="rdf:type">
						<xsl:attribute name="rdf:resource" select="'&crm;E30_Right'"/>
					</xsl:element>
					<xsl:element name="crm:P48_has_preferred_identifier"><xsl:value-of select="metadata/@usageTerms"/></xsl:element>
				</xsl:element>
				<xsl:element name="rdf:Description">
					<xsl:attribute name="rdf:about" select="metadata/@usageTerms"/>
					<xsl:element name="rdf:type">
						<xsl:attribute name="rdf:resource" select="'&crm;E42_Identifier'"/>
					</xsl:element>
				</xsl:element>
				
			<!-- Image -->
				<xsl:for-each select="derivative">
					<xsl:variable name="curFilename" select="@filename" />
					<!--xsl:variable name="imageUri" select="concat('&ycba_img_base;', $curFilename)" />
					<xsl:variable name="creationUri" select="concat($imageUri,'/creation')" /-->
					
					<xsl:element name="rdf:Description">
						<xsl:if test="@formatId = $DEEP_ZOOM_FORMAT_ID">
							<xsl:attribute name="rdf:about" select="concat('&yale_iiif;', ../@contentId)"/>
						</xsl:if>
						
						<xsl:if test="@formatId != $DEEP_ZOOM_FORMAT_ID">
							<xsl:attribute name="rdf:about" select="@url"/>
						</xsl:if>
						
						<!-- Image type -->
						<xsl:element name="rdf:type">
							<xsl:attribute name="rdf:resource" select="concat('&crm;', 'E38_Image')"/>
						</xsl:element>
						
						<!-- Image flags -->
						<xsl:variable name="flags" select="tokenize(../metadata/@caption, ', ')" />
						<xsl:for-each select="$flags">
							<xsl:variable name="index" select="position()"/>
							<xsl:variable name="flagPieces" select="tokenize($flags[$index], '\s+')" />
							<xsl:variable name="flagUri" select="concat('&ycba_img_flag;', string-join(($flagPieces), '_'))"/>
							<xsl:element name="bmo:PX_image_flag">
								<xsl:attribute name="rdf:resource" select="$flagUri"/>
							</xsl:element>
						</xsl:for-each>
						
						<!-- Image rights -->
						<xsl:element name="crm:P104_is_subject_to">
							<xsl:attribute name="rdf:resource" select="$usageUri"/>
						</xsl:element>
						
						<!-- Image bmo format -->
						<xsl:element name="bmo:PX_image_format">
							<xsl:variable name="formatUri" select="string-join(('&ycba_img_format;', @formatShort), '')"/>
							<xsl:attribute name="rdf:resource" select="$formatUri"/>
						</xsl:element>
						
						<!-- Image dimensions -->
						<xsl:element name="exif:height">
							<xsl:attribute name="rdf:datatype" select="'&xsd;integer'"/>
							<xsl:value-of select="@pixelsY"/>
						</xsl:element>
						<xsl:element name="exif:width">
							<xsl:attribute name="rdf:datatype" select="'&xsd;integer'"/>
							<xsl:value-of select="@pixelsX"/>
						</xsl:element>
						
						<!-- Image dc format -->
						<xsl:element name="dc:format"><xsl:value-of select="@format"/></xsl:element>

						<!-- Image creation -->
						<xsl:element name="crm:P94i_was_created_by">
							<xsl:variable name="creationUri" select="concat('&ycba;object/', ../@contentId,'/creation')" />
							<xsl:attribute name="rdf:resource" select="$creationUri"/>
						</xsl:element>
					
						<!-- Image - other images with the same features -->
						<xsl:if test="@formatId = $MAIN_IMG_FORMAT_ID">
							<xsl:for-each select="../derivative">
								<xsl:if test="@filename != $curFilename">
									<xsl:element name="crm:P130i_features_are_also_found_on">
										<xsl:if test="@formatId = $DEEP_ZOOM_FORMAT_ID">
											<xsl:attribute name="rdf:resource" select="concat('&yale_iiif;', ../@contentId)"/>
										</xsl:if>
										<xsl:if test="@formatId != $DEEP_ZOOM_FORMAT_ID">
											<xsl:attribute name="rdf:resource" select="@url"/>
										</xsl:if>
									</xsl:element>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
						
						<!-- Only for deep zoom images -->
						<xsl:if test="@formatId = $DEEP_ZOOM_FORMAT_ID">
							<xsl:element name="dct:conformsTo">
								<xsl:attribute name="rdf:resource" select="'http://library.stanford.edu/iiif/image­api/1.1/conformance.html#level1'"/>
							</xsl:element>
						</xsl:if>
					</xsl:element>
				</xsl:for-each>
				
			<!-- Creation -->
				<xsl:variable name="creationUri" select="concat('&ycba;object/', @contentId,'/creation')" />
				
				<xsl:element name="rdf:Description">
					<xsl:attribute name="rdf:about" select="$creationUri"/>
					<xsl:element name="rdf:type">
						<xsl:attribute name="rdf:resource" select="concat('&crm;', 'E65_Creation')"/>
					</xsl:element>
					<xsl:element name="crm:P14_carried_out_by">
						<!-- Assuming that YCBA produces all images -->
						<xsl:attribute name="rdf:resource" select="'&ycba_thesauri;ULAN/500303557'"/>
					</xsl:element>
					<xsl:element name="rdfs:label"><xsl:value-of select="metadata/@imageCredit"/></xsl:element>
				</xsl:element>
			</xsl:for-each>
		</rdf:RDF>
    </xsl:template>
</xsl:stylesheet>