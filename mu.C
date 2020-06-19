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
using namespace std;


void mu2(int pdgId) {
    TFile *MyFile = new TFile("out.root","READ");
    TString hname;
    if(pdgId == 13){
        hname ="mu-";
    }
    if(pdgId == -13){
        hname ="mu+";
    }
    TKey *key = MyFile->FindKey(hname);
    //TH1 *OriginZ = (TH1*)MyFile->Get(hname + "/ZOrigin");
    TH1 *hitCount = (TH1*)MyFile->Get(hname + "/corrEnergy1");
    TH1 *xyScore = (TH1*)MyFile->Get(hname + "/xVsyScore");
    TH1 *xyECal = (TH1*)MyFile->Get(hname + "/xVsyECal");
    TH1 *dxdy = (TH1*)MyFile->Get(hname +"/DelxVsDelY");
    TH1 *tdxdy = (TH1*)MyFile->Get(hname +"/TimeVsDelMag");
    TH1 *xyLowEn = (TH1*)MyFile->Get(hname +"/X vs Y Score < 0.1 GeV");
    
    TCanvas *c1 = new TCanvas("c1",hname,1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    hitCount->SetTitle("Number of Hits per Particle");
    hitCount->GetXaxis()->SetTitle("Number of Hits");
    hitCount->GetXaxis()->SetRangeUser(0., 10.);
    hitCount->GetYaxis()->SetRangeUser(0., 15000.);
    hitCount->Draw();
    for (int i =1; i < 6; i ++){
        hitCount = (TH1*)MyFile->Get(hname + "/corrEnergy" + i);
        hitCount->Draw("SAME");
    }
    c1->cd(2);
    xyScore->SetTitle("X Vs. Y Score Hits");
    xyScore->GetXaxis()->SetTitle("X (mm)");
    xyScore->GetYaxis()->SetTitle("Y (mm)");
    xyScore->Draw("COLZ");
    c1->cd(3);
    xyECal->SetTitle("X Vs. Y ECal Hits");
    xyECal->GetXaxis()->SetTitle("X (mm)");
    xyECal->GetYaxis()->SetTitle("Y (mm)");
    xyECal->Draw("COLZ");
    c1->cd(4);
    dxdy->SetTitle("Delta X vs Delta Y (TrackHits - ECalHits)");
    dxdy->GetXaxis()->SetTitle("dX (mm)");
    dxdy->GetYaxis()->SetTitle("dY (mm)");
    dxdy->Draw("COLZ");
    c1->cd(5);
    tdxdy->SetTitle("time vs distance");
    tdxdy->GetXaxis()->SetTitle("dX (mm)");
    tdxdy->GetYaxis()->SetTitle("dY (mm)");
    tdxdy->Draw("COLZ");
    c1->cd(6);
    xyLowEn->SetTitle("X vs Energy < 0.1 GeV");
    xyLowEn->GetXaxis()->SetTitle("X (mm)");
    xyLowEn->GetYaxis()->SetTitle("Y (mm)");
    xyLowEn->Draw("COLZ");
    c1->Print(hname + ".pdf(","pdf");

    TH1 *thetaX = (TH1*)MyFile->Get(hname +"/thetaX");
    TH1 *thetaY = (TH1*)MyFile->Get(hname +"/thetaY");
    TH1 *corrEnergy = (TH1*)MyFile->Get(hname +"/corrEnergy");
    TH1 *contEnergy = (TH1*)MyFile->Get(hname +"/contEnergy");
    TH1 *momentum = (TH1*)MyFile->Get(hname +"/momentum");
    TH1 *numHits = (TH1*)MyFile->Get(hname +"/numHits");
    TH1 *Xen = (TH1*)MyFile->Get(hname +"/xVsen");


    c1 = new TCanvas("c1",hname,1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    thetaX->SetTitle("theta X");
    thetaX->GetXaxis()->SetTitle("Degrees (rad)");
    thetaX->Draw();
    c1->cd(2);
    thetaY->SetTitle("theta Y");
    thetaY->GetXaxis()->SetTitle("Degrees (rad)");
    thetaY->Draw();
    c1->cd(3);
    corrEnergy->SetTitle("Energy Corrected and Contributed");
    corrEnergy->GetXaxis()->SetTitle("Energy (GeV)");
    corrEnergy->Draw();
    contEnergy->Draw("SAME");
    c1->cd(4);
    momentum->SetTitle("Particle Momentum");
    momentum->GetXaxis()->SetTitle("Momentum (GeV)");
    momentum->Draw();
    c1->cd(5);
    numHits->SetTitle("Number of Hits Per Particle");
    numHits->GetXaxis()->SetTitle("Number of Hits");
    numHits->Draw();
    c1->cd(6);
    Xen->SetTitle("X vs Energy");
    Xen->GetXaxis()->SetTitle("X (mm)");
    Xen->GetYaxis()->SetTitle("Energy (GeV)");
    Xen->Draw("COLZ");
    c1->Print(hname + ".pdf)","pdf");
    c1->Close();
    MyFile->Close();
/*
    hname = "mu+";
    OriginZ = (TH1*)MyFile->Get(hname + "/ZOrigin");
    xyScore = (TH1*)MyFile->Get(hname + "/xVsyScore");
    xyECal = (TH1*)MyFile->Get(hname + "/xVsyECal");
    dxdy = (TH1*)MyFile->Get(hname +"/DelxVsDelY");
    tdxdy = (TH1*)MyFile->Get(hname +"/TimeVsDelMag");

    c1 = new TCanvas("c1","mu+",1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    OriginZ->Draw();
    c1->cd(2);
    xyScore->Draw("COLZ");
    c1->cd(3);
    xyECal->Draw("COLZ");
    c1->cd(4);
    dxdy->Draw("COLZ");
    c1->cd(5);
    tdxdy->Draw("COLZ");
    c1->Print("mu+.pdf(","pdf");



    thetaX = (TH1*)MyFile->Get(hname +"/thetaX");
    thetaY = (TH1*)MyFile->Get(hname +"/thetaY");
    corrEnergy = (TH1*)MyFile->Get(hname +"/corrEnergy");
    contEnergy = (TH1*)MyFile->Get(hname +"/contEnergy");
    momentum = (TH1*)MyFile->Get(hname +"/momentum");
    numHits = (TH1*)MyFile->Get(hname +"/numHits");

    corrEnergy->SetLineColor(kRed);
    contEnergy->SetLineColor(kBlue);

    c1 = new TCanvas("c1","mu+",1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    thetaX->Draw();
    c1->cd(2);
    thetaY->Draw();
    c1->cd(3);
    corrEnergy->SetTitle("Energy");
    corrEnergy->Draw();
    contEnergy->Draw("SAME");
    c1->cd(4);
    momentum->Draw();
    c1->cd(5);
    numHits->Draw();
    c1->Print("mu+.pdf)","pdf");
    */
}

int mu(){
    int pdgId = 13;
    mu2(pdgId);
    pdgId = -13;
    mu2(pdgId);
    return 0;
}