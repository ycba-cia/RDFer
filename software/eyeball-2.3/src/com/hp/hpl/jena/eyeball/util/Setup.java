/*
 	(c) Copyright 2006, 2007, 2008 Hewlett-Packard Development Company, LP
 	All rights reserved.
 	$Id: Setup.java,v 1.3 2008/11/04 09:40:58 chris-dollin Exp $
*/

package com.hp.hpl.jena.eyeball.util;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.eyeball.assemblers.*;
import com.hp.hpl.jena.eyeball.vocabulary.EYE;

/**
    Static methods for setup or other preparation (at least initially just for
    declaring the Eyeball assemblers).
    
    @author kers
*/
public class Setup
    {
    /**
        Add the Eyeball assemblers to the Assembler.general collection.
    */
    public static void declareEyeballAssemblers()
        {
        Assembler.general
            .implementWith( EYE.Inspector, new InspectorAssembler() )
            .implementWith( EYE.Analysis, new AnalysisAssembler() )
            .implementWith( EYE.Doctor, new DoctorAssembler() )
            .implementWith( EYE.Eyeball, new EyeballAssembler() )
            .implementWith( EYE.Renderer, new RendererAssembler() )
            ;
        }
    }

