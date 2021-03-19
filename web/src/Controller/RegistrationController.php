<?php

namespace App\Controller;

use App\ApiClient\MemberRegistrationService;
use App\ApiClient\RegistrationService;
use App\ApiClient\UserService;
use App\Entity\Member;
use App\Forms\Membership\MemberListForm;
use App\Forms\Membership\RegistrationForm;
use App\Menus\TopMenuProvider;
use Knp\Menu\FactoryInterface;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RegistrationController extends AbstractController implements
    TopMenuProvider
{
    /**
     * @var $registrationService RegistrationService
     */
    private $registrationService;

    /**
     * @var $logger LoggerInterface
     */
    private $logger;

    /**
     * @var $userService UserService
     */
    private $userService;

    /**
     * RegistrationController constructor.
     * @param MemberRegistrationService $registrationService
     * @param LoggerInterface $logger
     * @param UserService $userService
     */
    public function __construct(
        MemberRegistrationService $registrationService,
        LoggerInterface $logger,
        UserService $userService
    ) {
        $this->registrationService = $registrationService;
        $this->logger = $logger;
        $this->userService = $userService;
    }

    /**
     * @Route("/register", name="registration")
     * @param Request $request
     * @return Response
     */
    public function index(Request $request): Response
    {
        $me = $this->userService->me();

        $form = $this->createForm(MemberListForm::class, $me->getMembers());

        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $buttonName = $form->getClickedButton()->getName();
            switch ($buttonName) {
                case "add-member":
                    $this->logger->debug("Form submitted");
                    return $this->redirectToRoute("add-member");
                default:
                    $this->logger->debug(
                        "Unhandled submit button: " . $buttonName
                    );
                    break;
            }
        }
        return $this->render("registration/index.html.twig", [
            "form" => $form->createView(),
        ]);
    }

    /**
     * @Route("/register/add-member", name="add-member")
     * @param Request $request
     * @return Response
     */
    public function addMember(Request $request): Response
    {
        $me = $this->userService->me();

        $form = $this->createForm(RegistrationForm::class, new Member(), [
            "category" => $this->registrationService->getCategory("junior"),
            "translation_domain" => "membership",
        ]);

        $form->handleRequest($request);
        return $this->render("registration/index.html.twig", [
            "form" => $form->createView(),
        ]);
    }

    public static function getFrontEndMenuItems(
        FactoryInterface $factory
    ): array {
        return [];
    }

    public static function getTopMenuItems(FactoryInterface $factory): array
    {
        return [
            $factory
                ->createItem("register", [
                    "route" => "registration",
                ])
                ->setAttribute("sortorder", 10),
        ];
    }

    public static function getContentAdminMenuItems(
        FactoryInterface $factory
    ): array {
        return [];
    }

    public static function getAdministrationMenuItems(
        FactoryInterface $factory
    ): array {
        return [];
    }

    public static function getConfigurationAdminMenuItems(
        FactoryInterface $factory
    ): array {
        // TODO: Implement getConfigurationAdminMenuItems() method.
    }
}
