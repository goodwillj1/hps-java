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
#include <TBenchmark.h>
#include <TInterpreter.h>
#include <iostream>
using namespace std;


void plot2016() {

    TFile *MyFile = new TFile("2016ex.root","READ");
    //if ( MyFile->IsOpen() ) printf("File opened successfully\n");

    //TH1D *asdf = new TH1D("asdf", "Electrons;Track Momentum (GeV);Counts", 100, 0., 6.);

    TH1 *etrackmom = (TH1*)MyFile->Get("electron/TrackMomentum");
    TH1 *eTheta = (TH1*)MyFile->Get("electron/Theta");
    TH1 *eChi2 = (TH1*)MyFile->Get("electron/Chi2");
    TH2 *exy = (TH2*)MyFile->Get("electron/xVsyAtECAl");
    TH2 *edxdy = (TH2*)MyFile->Get("electron/delxVsdely");
    TH1 *eCaltoTrack = (TH1*)MyFile->Get("electron/CaltoTrack");
    TH1 *eMom = (TH1*)MyFile->Get("electron/Momentum");
    TH1 *eEn = (TH1*)MyFile->Get("electron/Energy");
    TH1 *eEnmom = (TH1*)MyFile->Get("electron/EnergyVsmom");
    TH1 *eEnDivmom = (TH1*)MyFile->Get("electron/Energy\\mom");
    
    etrackmom->SetLineColor(kRed);
    eTheta->SetLineColor(kRed);
    eChi2->SetLineColor(kRed);
    exy->SetFillColor(kRed);
    edxdy->SetLineColor(kRed);
    eCaltoTrack->SetLineColor(kRed);
    eEnmom->SetLineColor(kRed);
    eEnDivmom->SetLineColor(kRed);
    eMom->SetLineColor(kRed);
    eEn->SetLineColor(kRed);


    TH1 *ptrackmom= (TH1*)MyFile->Get("positron/TrackMomentum");
    TH1 *pTheta = (TH1*)MyFile->Get("positron/Theta");
    TH1 *pChi2 = (TH1*)MyFile->Get("positron/Chi2");
    TH2 *pxy = (TH2*)MyFile->Get("positron/xvsyAtECAl");
    TH2 *pdxdy = (TH2*)MyFile->Get("positron/delXvsDelY");
    TH1 *pCaltoTrack = (TH1*)MyFile->Get("positron/CaltoTrack");
    TH1 *pEn = (TH1*)MyFile->Get("positron/Energy");
    TH1 *pMom = (TH1*)MyFile->Get("positron/Momentum");
    TH1 *pEnmom = (TH1*)MyFile->Get("positron/EnergyVsmom");
    TH1 *pEnDivmom = (TH1*)MyFile->Get("positron/Energy\\mom");

    ptrackmom->SetLineColor(kBlue);

    pTheta->SetLineColor(kBlue);

    pChi2->SetLineColor(kBlue);
    pxy->SetFillColor(kBlue);
    pdxdy->SetLineColor(kBlue);
    pCaltoTrack->SetLineColor(kBlue);
    pEnmom->SetLineColor(kBlue);
    pEnDivmom->SetLineColor(kBlue);
    pEn->SetLineColor(kBlue);
    pMom->SetLineColor(kBlue);

    //cout<<asdf<<endl;


    TCanvas *c1 = new TCanvas("c1","First",1000,1000);
    c1->Divide(2,4);
    c1->cd(1);
    etrackmom->Draw();
    ptrackmom->Draw("SAME");
    etrackmom->SetTitle("Track Momentum");
    etrackmom->GetXaxis()->SetTitle("Momentum");
    etrackmom->GetYaxis()->SetTitle("Counts");
    
    TLegend *leg = new TLegend(10.,10.,10.,10.);
    leg->AddEntry(etrackmom,"Electron","l");
    leg->AddEntry(ptrackmom,"Positron","l");
    leg->Draw();
    
    c1->cd(2);
    eTheta->Draw();
    pTheta->Draw("SAME");
    eTheta->SetTitle("Theta");
    eTheta->GetXaxis()->SetTitle("Theta (rad)");
    eTheta->GetYaxis()->SetTitle("Counts");
    c1->cd(3);
    eChi2->Draw();
    pChi2->Draw("SAME");
    eChi2->SetTitle("Chi^2");
    eChi2->GetXaxis()->SetTitle("Degrees of Freedom");
    eChi2->GetYaxis()->SetTitle("Counts");
    c1->cd(4);
    exy->Draw("box");
    pxy->Draw("box same");
    exy->SetTitle("X Vs Y at Calorimeter");
    exy->GetXaxis()->SetTitle("X (mm)");
    exy->GetYaxis()->SetTitle("Y (mm)");
    
    c1->cd(5);
    edxdy->Draw("box");
    pdxdy->Draw("box same");
    edxdy->SetTitle("Delta X Vs Delta Y");
    edxdy->GetXaxis()->SetTitle("X (mm)");
    edxdy->GetYaxis()->SetTitle("Y (mm)");

    c1->cd(6);
    eCaltoTrack->Draw();
    pCaltoTrack->Draw("SAME");
    eCaltoTrack->SetTitle("Distance of Calorimeter to Track");
    eCaltoTrack->GetXaxis()->SetTitle("X (mm)");
    eCaltoTrack->GetYaxis()->SetTitle("Y (mm)");

    c1->cd(7);
    eEnmom->Draw("box");
    pEnmom->Draw("box same");
    eEnmom->SetTitle("Energy Vs Momentum");
    eEnmom->GetXaxis()->SetTitle("Momentum (GeV)");
    eEnmom->GetYaxis()->SetTitle("Energy (GeV)");

    c1->cd(8);
    eEnDivmom->Draw("box");
    pEnDivmom->Draw("box same");
    eEnDivmom->SetTitle("Energy//Momentum");
    eEnDivmom->GetYaxis()->SetTitle("Counts");
    eEnDivmom->GetXaxis()->SetTitle("Energy//Momentum");

    TCanvas *c2 = new TCanvas("c2","Second", 1000,1000);
    c2->Divide(2,1);
    c2->cd(1);
    eEn->Fit("gaus");
    eMom->Fit("gaus");
    eEn->SetTitle("Electron Energy and Momentum");
    eEn->GetYaxis()->SetTitle("Counts");
    eEn->GetXaxis()->SetTitle("Energy and Momentum (GeV)");
    eEn->Draw();
    eMom->Draw("SAME");


    c2->cd(2);
    pEn->Fit("gaus");
    pMom->Fit("gaus");
    pEn->SetTitle("Positron Energy and Momentum");
    pEn->GetYaxis()->SetTitle("Counts");
    pEn->GetXaxis()->SetTitle("Energy and Momentum (GeV)");
    pEn->Draw();
    pMom->Draw("SAME");

}

