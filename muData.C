#include <TFile.h>
//#include <TNtuple.h>
#include <TH1.h>
#include <TH2.h>
//#include <TProfile.h>
#include <TCanvas.h>
#include <TLegend.h>
//#include <TFrame.h>
//#include <TROOT.h>
//#include <TSystem.h>
//#include <TRandom3.h>
#include "TRint.h"
#include "TROOT.h"
#include "TKey.h"
#include "TDirectory.h"
#include <TBenchmark.h>
#include <TInterpreter.h>
#include <iostream>
#include <string>
using namespace std;


void mu2(int pdgId) {
    TFile *MyFile = new TFile("out.root","READ");
    TString hname;
    if(pdgId == 11){
        hname ="mu-";
    }
    if(pdgId == -11){
        hname ="mu+";
    }
    TKey *key = MyFile->FindKey(hname);
    //TH1 *OriginZ = (TH1*)MyFile->Get(hname + "/ZOrigin");
    TH1 *TrackMomentum = (TH1*)MyFile->Get(hname + "/TrackMomentum");
    TH1 *Theta = (TH1*)MyFile->Get(hname + "/Theta");
    TH1 *chi2 = (TH1*)MyFile->Get(hname + "/chi2");
    TH1 *xVsyAtECAl = (TH1*)MyFile->Get(hname +"/xvsyAtECAl");
    TH1 *delxVsdely = (TH1*)MyFile->Get(hname +"/delxVsdely");
    TH1 *CaltoTrack = (TH1*)MyFile->Get(hname +"/CaltoTrack");
    TH1 *EnergyVsmom = (TH1*)MyFile->Get(hname + "/EnergyVsmom");
    TH1 *EnergyDivMom = (TH1*)MyFile->Get(hname + "/Energy\\mom");
    TH1 *En = (TH1*)MyFile->Get(hname + "/Energy");
    TH1 *mom = (TH1*)MyFile->Get(hname + "/Momentum");
    TCanvas *c1 = new TCanvas("c1",hname,1000,1000);
    c1->Divide(3,3);
    c1->cd(1);
    TrackMomentum->SetTitle("Track Momentum");
    TrackMomentum->Draw();
    c1->cd(2);
    Theta->SetTitle("Theta");
    Theta->Draw();
    c1->cd(3);
    chi2->SetTitle("Chi2");
    chi2->Draw();
    c1->cd(4);
    xVsyAtECAl->SetTitle("X vs Y at ECal");
    xVsyAtECAl->Draw("COLZ");
    c1->cd(5);
    delxVsdely->SetTitle("Delta X vs Delta Y");
    delxVsdely->Draw("COLZ");
    c1->cd(6);
    CaltoTrack->SetTitle("Distance Calorimeter to Track");
    CaltoTrack->Draw();
    c1->cd(7);
    EnergyVsmom->SetTitle("Energy vs Momentum");
    EnergyVsmom->Draw("COLZ");
    c1->cd(8);
    EnergyDivMom->SetTitle("Energy Divided Momementum");
    EnergyDivMom->Draw();
    c1->cd(9);
    En->SetTitle("Energy");
    En->Draw();
    c1->Print(hname + "Data.pdf)","pdf");
    c1->Close();
    MyFile->Close();
}

int muData(){
    int pdgId = 11;
    mu2(pdgId);
    pdgId = -11;
    mu2(pdgId);
    return 0;
}