/*
 * Created by Grzegorz Kostkowski
 */
package com.pwr.zpi.core.memory.episodic;

import com.pwr.zpi.core.Agent;
import com.pwr.zpi.exceptions.InvalidFormulaException;
import com.pwr.zpi.exceptions.NotConsistentDKException;
import com.pwr.zpi.core.memory.semantic.IndividualModel;
import com.pwr.zpi.io.Configuration;
import com.pwr.zpi.language.Formula;
import com.pwr.zpi.language.Grounder;
import com.pwr.zpi.language.Trait;
import com.sun.istack.internal.NotNull;
import javafx.util.Pair;

import java.util.*;

/**
 * Class represents distribution of knowledge as  a set of "classes" established for given moment in time,
 * for certain simple or complex relatedFormula and associated individualModel. Number of classes depends on kind of grounded relatedFormula
 * (which induces mental models).
 * Note: By default, each instance of this class contains knowledge distribution for ONE relatedFormula (and therefore ONE mental model).
 * This behaviour can be extended thorough passing appropriate flag to constructor.
 * Regardless mentioned flag, each knowledge distribution contains complete collection of grounding sets.
 * RA1 - Represents set of all base profiles, which are included in working memory and presents individualModel as described by
 * given trait.
 * TA1 - Represents set of all base profiles, which are included in long-term memory and presents individualModel as described by
 * given trait.
 * Etc.
 *
 * @author Grzegorz Kostkowski
 */
public class DistributedKnowledge implements Cloneable {
    /**
     * Describes possible versions of distributed knowledge: Simple if false (building only dk classes associated
     * with given relatedFormula) or complex if true (building all dk classes - also for mental models related to
     * complementary formulas)
     */
    private static final boolean DK_IS_COMPLEX = Configuration.DK_IS_COMPLEX;


    /**
     * Describes what kind of knowledge distribution this instance represents.
     */
    private boolean dkIsComplex;
    private final int timestamp;
    private final Formula relatedFormula;
    private final List<Trait> traits;
    private final IndividualModel individualModel;
    private Set<BaseProfile> inLM;
    private Set<BaseProfile> inWM;

    private BPCollection relatedObservationsBase;

    /**
     * Complementary formulas for this.relatedFormula. For convenience, complementary formulas contains also this.relatedFormula.
     */
    private List<Formula> complementaryFormulas = new ArrayList<>();

    private Map<Formula, Set<BaseProfile>> groundingSetsMap;
    /**
     * Map of classes for this knowledge distribution. Exact class is certain value in map
     * and associated key is pair which represent mental model (relatedFormula) and memory type which are used
     * to build such class.
     */
    private Map<Pair<Formula, BPCollection.MemoryType>, Set<BaseProfile>> dkClasses;


    public DistributedKnowledge(Agent agent, Formula formula, int timestamp, boolean makeCompleteDistribution)
            throws InvalidFormulaException, NotConsistentDKException {
        if (agent == null || formula == null)
            throw new NullPointerException("One of parameters is null.");
        if (timestamp < 0)
            throw new IllegalStateException("Not valid timestamp.");

        this.timestamp = timestamp;
        this.relatedFormula = formula;
        this.traits = formula.getTraits();
        this.individualModel = formula.getModel();
        dkClasses = new HashMap<>();
        dkIsComplex = makeCompleteDistribution;

        relatedObservationsBase = agent.getKnowledgeBase();

        inLM = relatedObservationsBase.getBaseProfiles(timestamp, BPCollection.MemoryType.LM);
        inWM = relatedObservationsBase.getBaseProfiles(timestamp, BPCollection.MemoryType.WM);

//        if (makeCompleteDistribution)
        complementaryFormulas = formula.getComplementaryFormulas();
        groundingSetsMap = Grounder.getGroundingSets(complementaryFormulas, BPCollection.asBaseProfilesSet(inWM, inLM));
        /*else {
            groundingSetsMap = new HashMap<>();
            groundingSetsMap.put(relatedFormula,
                    Grounder.getGroundingSet(relatedFormula, BPCollection.asBaseProfilesSet(inWM, inLM)));
        }*/

        if (makeCompleteDistribution)
            for (Formula cformula : complementaryFormulas) {
                setDkClass(inWM, cformula, BPCollection.MemoryType.WM);
                setDkClass(inLM, cformula, BPCollection.MemoryType.LM);
            }
        else {
            setDkClass(inWM, formula, BPCollection.MemoryType.WM);
            setDkClass(inLM, formula, BPCollection.MemoryType.LM);
        }

        checkIfConsistent();

    }

    public DistributedKnowledge(Agent agent, Formula formula)
            throws InvalidFormulaException, NotConsistentDKException {
        this(agent, formula, agent.getKnowledgeBase().getTimestamp(), DK_IS_COMPLEX);
    }

    public DistributedKnowledge(Agent agent, Formula formula, int timestamp)
            throws InvalidFormulaException, NotConsistentDKException {
        this(agent, formula, timestamp, false);
    }

    public DistributedKnowledge(Agent agent, Formula formula, boolean makeCompleteDistribution)
            throws InvalidFormulaException, NotConsistentDKException {
        this(agent, formula, agent.getKnowledgeBase().getTimestamp(), makeCompleteDistribution);
    }

    private DistributedKnowledge(boolean dkIsComplex, int timestamp, Formula relatedFormula, List<Trait> traits,
                                IndividualModel individualModel, Set<BaseProfile> inLM, Set<BaseProfile> inWM,
                                BPCollection relatedObservationsBase, List<Formula> complementaryFormulas,
                                Map<Formula, Set<BaseProfile>> groundingSetsMap,
                                 Map<Pair<Formula, BPCollection.MemoryType>, Set<BaseProfile>> dkClasses) {
        this.dkIsComplex = dkIsComplex;
        this.timestamp = timestamp;
        this.relatedFormula = relatedFormula;
        this.traits = traits;
        this.individualModel = individualModel;
        this.inLM = inLM;
        this.inWM = inWM;
        this.relatedObservationsBase = relatedObservationsBase;
        this.complementaryFormulas = complementaryFormulas;
        this.groundingSetsMap = groundingSetsMap;
        this.dkClasses = dkClasses;
    }


    /**
     * Performs checking to ensure that knowledge distribution was built in proper way.
     */
    private void checkIfConsistent() throws NotConsistentDKException {
        if (!dkIsComplex)
            makeChecking(relatedFormula);
        else for (Formula currFormula : complementaryFormulas)
            makeChecking(currFormula);
    }

    /**
     * Performs checking for classes associated with single mental model.
     *
     * @param formula
     * @throws NotConsistentDKException
     */
    private void makeChecking(Formula formula) throws NotConsistentDKException {
        Set<BaseProfile> ra = getDkClassByDesc(formula, BPCollection.MemoryType.WM),
                ta = getDkClassByDesc(formula, BPCollection.MemoryType.LM);
        Set<BaseProfile> groundingSet = groundingSetsMap.get(formula);

        Set<BaseProfile> intersection1 = new HashSet<BaseProfile>(inWM);
        intersection1.retainAll(groundingSet);
        if (!ra.containsAll(intersection1))
            throw new NotConsistentDKException();
        Set<BaseProfile> intersection2 = new HashSet<BaseProfile>(inLM);
        intersection2.retainAll(groundingSet);
        if (!ta.containsAll(intersection2))
            throw new NotConsistentDKException();
        Set<BaseProfile> intersection3 = new HashSet<BaseProfile>(ra);
        intersection3.retainAll(ta);
        if (!intersection3.isEmpty())
            throw new NotConsistentDKException();
        Set<BaseProfile> sum = new HashSet<BaseProfile>(ra);
        sum.addAll(ta);
        if (!sum.containsAll(groundingSet))
            throw new NotConsistentDKException();
    }

    private void setDkClass(Set<BaseProfile> affectedMemory, Formula formula,
                            BPCollection.MemoryType memType) {
        Set<BaseProfile> currClass = new HashSet<>();
        dkClasses.put(new Pair<>(formula, memType), currClass);
        currClass.addAll(affectedMemory);
        currClass.retainAll(groundingSetsMap.get(formula));
    }


    /**
     * Return knowledge distribution classes. Note that if there are no base profile that fulfills requirements then
     * empty map entries will be returned.
     *
     * @return
     */
    @NotNull
    public Map<Pair<Formula, BPCollection.MemoryType>, Set<BaseProfile>> getDistributionClasses() {
        return dkClasses;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public IndividualModel getObservation() {
        return individualModel;
    }

    public List<Trait> getTraits() throws InvalidFormulaException {
        return traits;
    }

    public Formula getFormula() {
        return relatedFormula;
    }

    public List<Formula> getComplementaryFormulas() {
        /*List<Formula> res=new ArrayList<>();
        Collections.copy(complementaryFormulas, res);
        return res;*/
        return complementaryFormulas;
    }

    /**
     * Gives class according to given relatedFormula (mental model) and memory type.
     *
     * @return
     */
    public Set<BaseProfile> getDkClassByDesc(Formula formula, BPCollection.MemoryType mem) {
        return dkClasses.get(new Pair<>(formula, mem));
    }

    public Set<BaseProfile> getGroundingSet(Formula formula) {
        TreeMap<Formula, Set<BaseProfile>> res = new TreeMap<>();
        res.putAll(groundingSetsMap);
        return res.get(formula);
    }

    public boolean isDkComplex() {
        return dkIsComplex;
    }

    public BPCollection getRelatedObservationsBase() {
        return relatedObservationsBase;
    }

    public boolean isRelated(Formula formula) {
        return formula.equals(this.relatedFormula) || complementaryFormulas.contains(formula);
    }

    public Map<Formula, Set<BaseProfile>> mapOfGroundingSets() {
        return groundingSetsMap;
    }

    /**
     * Method determines if given distributed knowledge is more recent than this one.
     * One dk is understood as more recent than other if:
     * <ul>
     * <li>has greater value of timestamp</li>
     * <li>Number of included base profiles is greater (when timestamps are the same)</li>
     * <li>Number of included base profiles in working memory is greater (when general sizes of episodic base
     * are the same)</li>
     * <li>If none of above is applied, then given dk is treated as up-to-date.</li>
     * </ul>
     *
     * @param dk Examined distributedKnowledge.
     * @return False if provided dk is newer; true otherwise.
     */
    public boolean isNewerThan(DistributedKnowledge dk) {
        if (timestamp != dk.getTimestamp()) return timestamp > dk.getTimestamp();
        if (relatedObservationsBase.getEpisodicBaseSize() != dk.relatedObservationsBase.getEpisodicBaseSize())
            return relatedObservationsBase.getEpisodicBaseSize() > dk.relatedObservationsBase.getEpisodicBaseSize();
        return relatedObservationsBase.getEpisodicBaseSize(BPCollection.MemoryType.WM)
                > dk.relatedObservationsBase.getEpisodicBaseSize(BPCollection.MemoryType.WM);
    }

    public Map<Formula, Set<BaseProfile>> getGroundingSetsMap() {
        return groundingSetsMap;
    }


    @Override
    public DistributedKnowledge clone() throws CloneNotSupportedException {
        return new DistributedKnowledge(dkIsComplex, timestamp, relatedFormula, new ArrayList<>(traits), individualModel,
                new HashSet<>(inLM), new HashSet<>(inWM), relatedObservationsBase,
                new ArrayList<>(complementaryFormulas), new HashMap<>(groundingSetsMap),
                new HashMap<>(dkClasses));
    }
}
